package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Dto.CommentLikeDto;
import com.m2i.showtime.yak.Dto.CommentNotifDto;
import com.m2i.showtime.yak.Dto.Search.CommentGetDto;
import com.m2i.showtime.yak.Dto.UserSimpleDto;
import com.m2i.showtime.yak.Dto.userCommentDto;
import com.m2i.showtime.yak.Entity.Comment;
import com.m2i.showtime.yak.Entity.Like;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Jwt.JwtConfig;
import com.m2i.showtime.yak.Repository.CommentRepository;
import com.m2i.showtime.yak.Repository.LikeRepository;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CommentService {
    private final MovieService movieService;
    private final MovieRepository movieRepository;

//    private final KafkaMessageGeneratorService kafkaMessageGeneratorService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final String UserNotFound="User not found";
    private final LikeRepository likeRepository;


//    public CommentService(MovieService movieService, MovieRepository movieRepository, KafkaMessageGeneratorService kafkaMessageGeneratorService, UserRepository userRepository, CommentRepository commentRepository, SecretKey secretKey, JwtConfig jwtConfig, LikeRepository likeRepository) {
//        this.movieService = movieService;
//        this.movieRepository = movieRepository;
//        this.kafkaMessageGeneratorService = kafkaMessageGeneratorService;
//        this.userRepository = userRepository;
//        this.commentRepository = commentRepository;
//        this.secretKey = secretKey;
//        this.jwtConfig = jwtConfig;
//        this.likeRepository = likeRepository;
//    }
    public CommentService(MovieService movieService,
                          MovieRepository movieRepository,
                          UserRepository userRepository,
                          CommentRepository commentRepository,
                          SecretKey secretKey, JwtConfig jwtConfig,
                          LikeRepository likeRepository) {
        this.movieService = movieService;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
        this.likeRepository = likeRepository;
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

    public List<CommentGetDto> getComments(int movieId, UserSimpleDto userSimpleDto) {
        List<CommentGetDto> response = new ArrayList<>();
        Optional<Comment[]> commentOptional = this.commentRepository.getCommentsByMovieId((long) movieId);
        User user = this.userRepository.findById(userSimpleDto.getId()).orElseThrow(() -> new IllegalStateException(UserNotFound));
        for (Comment comment : commentOptional.get()) {
            CommentGetDto commentGetDto = new CommentGetDto();
            commentGetDto.setComments(comment);
            Optional<Like> like = this.likeRepository.getLikeByCommentIdAndUserId(comment.getId(), user.getId());
            if (like.isPresent()) {
                commentGetDto.setLiked(true);
            }
            response.add(commentGetDto);

        }

        return response;
    }

    public List<CommentGetDto> getUserComments(int movieId, UserSimpleDto userSimpleDto) {
        //A changer token
        List<CommentGetDto> response = new ArrayList<>();
        User user = this.userRepository.findById(userSimpleDto.getId()).orElseThrow(() -> new IllegalStateException(UserNotFound));
        Optional<Comment[]> comments = this.commentRepository.getUserCommentsByMovieIdAndUserId((long) movieId, (long) user.getId());
        for (Comment comment : comments.get()) {
            LocalDateTime pubishDate = comment.getDatePublication();
            LocalDateTime now = LocalDateTime.now();
            if (pubishDate.plusDays(1).isBefore(now) && comment.isValidate() == true && comment.isSpoiler() == false) {
                comment.setDeleted(true);
                this.commentRepository.save(comment);
            } else if (comment.isDeleted() == false) {
                CommentGetDto commentGetDto = new CommentGetDto();
                commentGetDto.setComments(comment);
                Optional<Like> like = this.likeRepository.getLikeByCommentIdAndUserId(comment.getId(), user.getId());
                if (like.isPresent()) {
                    commentGetDto.setLiked(true);
                }
                response.add(commentGetDto);
            }
        }
        return response;
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
//            this.kafkaMessageGeneratorService.sendCommentNotif(commentNotifDto);
            this.commentRepository.save(comment1);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public CommentGetDto likeComment(UserSimpleDto userSimpleDto, CommentLikeDto commentLikeDto) {
        User user = this.userRepository.findById(userSimpleDto.getId()).orElseThrow(() -> new IllegalStateException(UserNotFound));
        CommentGetDto commentGetDto = new CommentGetDto();
        Optional<Comment> comment = this.commentRepository.findById(commentLikeDto.getCommentId());
        if (comment.isEmpty()) throw new IllegalStateException("Comment not found");
        Like likeUser = this.likeRepository.findbyUserIdAndCommentId(user.getId(), comment.get().getId());
        if (likeUser == null) {
            Like like = new Like();
            like.setCommentId(comment.get().getId());
            like.setUserId(user.getId());
            this.likeRepository.save(like);
            comment.get().getLikes().add(like);
            this.commentRepository.save(comment.get());
            commentGetDto.setComments(comment.get());
            Optional<Like> likeResponse = this.likeRepository.getLikeByCommentIdAndUserId(comment.get().getId(), user.getId());
            if (likeResponse.isPresent()) {
                commentGetDto.setLiked(true);
            }
        } else {
            comment.get().getLikes().remove(likeUser);
            this.commentRepository.save(comment.get());
            this.likeRepository.delete(likeUser);
            commentGetDto.setComments(comment.get());
        }
        return commentGetDto;
    }
}
