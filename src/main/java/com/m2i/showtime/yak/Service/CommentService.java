package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Dto.CommentNotifDto;
import com.m2i.showtime.yak.Dto.userCommentDto;
import com.m2i.showtime.yak.Entity.Comment;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Jwt.JwtConfig;
import com.m2i.showtime.yak.Repository.CommentRepository;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final MovieService movieService;
    private final MovieRepository movieRepository;
    private final KafkaMessageGeneratorService kafkaMessageGeneratorService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final String UserNotFound="User not found";


    public CommentService(MovieService movieService, MovieRepository movieRepository, KafkaMessageGeneratorService kafkaMessageGeneratorService, UserRepository userRepository, CommentRepository commentRepository, SecretKey secretKey, JwtConfig jwtConfig) {
        this.movieService = movieService;
        this.movieRepository = movieRepository;
        this.kafkaMessageGeneratorService = kafkaMessageGeneratorService;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
    }

    public boolean saveComment(userCommentDto userCommentDto) {
        try {
            User user = this.userRepository.findUserByEmail(userCommentDto.getUserMail()).orElseThrow(() -> new IllegalStateException(UserNotFound));
            Comment comment = new Comment();
            comment.setContent(userCommentDto.getCommentText());
            comment.setUser(user);
            comment.setMovie_id(userCommentDto.getMovieId());
            this.commentRepository.save(comment);
            movieService.getMovieOrCreateIfNotExist(userCommentDto.getMovieId(), userCommentDto.getMovieTitle());
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Comment> getComments(int movieId) {
        Comment[] comments = this.commentRepository.getCommentsByMovieId((long) movieId);
        List<Comment> commentList = Arrays.asList(comments);
        return commentList;
    }

    public List<Comment> getUserComments(int movieId, String token) {
        token = token.replace(jwtConfig.getTokenPrefix(), "");
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        User user = this.userRepository.findUserByEmail(claimsJws.getBody().getSubject()).orElseThrow(() -> new IllegalStateException(UserNotFound));
        Comment[] comments = this.commentRepository.getUserCommentsByMovieIdAndUserId((long) movieId, (long) user.getId());
        List<Comment> commentList = new LinkedList<Comment>(Arrays.asList(comments));
        for (Comment comment : commentList) {
            LocalDateTime pubishDate = comment.getDatePublication();
            LocalDateTime now = LocalDateTime.now();
            if (pubishDate.plusDays(1).isBefore(now) && comment.isValidate() == false && comment.isSpoiler() == true) {
                comment.setDeleted(true);
                commentList.remove(comment);
                this.commentRepository.save(comment);
            }
        }
        return commentList;
    }

    public List<Comment> getAllComments() {
        List<Comment> commentList = this.commentRepository.findAll();
        return commentList;
    }

    public List<Comment> getcommentbyId(int id) {
        Comment[] comments = new Comment[]{this.commentRepository.getById((long) id)};
        List<Comment> commentList = Arrays.asList(comments);
        return commentList;
    }

    public boolean validateComment(Long commentId) {
        try {
            Comment comment1 = this.commentRepository.getById(commentId);
            comment1.setValidate(true);
            this.commentRepository.save(comment1);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean rejectComment(Long commentId) {
        try {
            Comment comment1 = this.commentRepository.getById(commentId);
            comment1.setValidate(false);
            this.commentRepository.save(comment1);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean spoilComment(CommentNotifDto commentNotifDto) {
        try {
            Comment comment1 = this.commentRepository.getById(commentNotifDto.getComment_id());
            comment1.setSpoiler(true);
            comment1.setValidate(false);
            this.commentRepository.save(comment1);
            Optional<Movie> currentMovie = movieRepository.findByTmdbId(comment1.getMovie_id());
            String message = "Votre commentaire sur le film " + currentMovie.get().getName() + " a été signalé comme spoil";
            JSONObject data = new JSONObject();
            data.put("message",message);
            data.put("status","rejected");
            commentNotifDto.setUsername(comment1.getUser().getUsername());
            commentNotifDto.setMessage(data.toString());
            kafkaMessageGeneratorService.sendCommentNotif(commentNotifDto);
            this.commentRepository.save(comment1);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

}
