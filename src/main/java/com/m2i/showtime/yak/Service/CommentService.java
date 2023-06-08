package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Dto.Search.CommentGetDto;
import com.m2i.showtime.yak.Entity.*;

import com.m2i.showtime.yak.Repository.*;

import com.m2i.showtime.yak.Service.User.UserService;

import com.m2i.showtime.yak.common.comment.CommentType;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CommentService {
    private final MovieService movieService;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final String UserNotFound="User not found";
    private final LikeRepository likeRepository;

    private final UserService userService;
    private final ResponseRepository responseRepository;

    public CommentService(MovieService movieService, MovieRepository movieRepository, UserRepository userRepository, CommentRepository commentRepository, LikeRepository likeRepository, UserService userService,
                          ResponseRepository responseRepository) {
        this.movieService = movieService;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.responseRepository = responseRepository;
    }

    public boolean saveComment(userCommentDto userCommentDto) {
        try {
            User user = this.userRepository.findUserByEmail(userCommentDto.getUserMail()).orElseThrow(() -> new IllegalStateException(UserNotFound));
            Comment comment = new Comment();
            CommentType commentType = CommentType.valueOf(userCommentDto.getTypeElement().toUpperCase());
            comment.setContent(userCommentDto.getCommentText());
            comment.setUser(user);
            comment.setElement_id(userCommentDto.getElementId());
            comment.setTypeElement(commentType);
            this.commentRepository.save(comment);
            movieService.getMovieOrCreateIfNotExist(userCommentDto.getElementId(), userCommentDto.getElementTitle());
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<CommentGetDto> getComments(long elementId, UserSimpleDto userSimpleDto, String type) {
        List<CommentGetDto> response = new ArrayList<>();
        Optional<Comment[]> commentOptional = null;
        if(type.equals("movie")){
            commentOptional = this.commentRepository.getCommentsByTypeAndId((long) elementId,CommentType.MOVIE);
        }
        if(type.equals("serie")){
            commentOptional = this.commentRepository.getCommentsByTypeAndId((long) elementId,CommentType.SERIE);
        }


        User user = this.userRepository.findById(userSimpleDto.getId()).orElseThrow(() -> new IllegalStateException(UserNotFound));
        for (Comment comment : commentOptional.get()) {
            CommentGetDto commentGetDto = new CommentGetDto();
            comment.getUser().setComments(null);
            comment.getUser().setPassword(null);
            comment.getUser().setFollowers(null);
            comment.getUser().setFollowing(null);
            commentGetDto.setComments(comment);
            Optional<Like> like = this.likeRepository.getLikeByCommentIdAndUserId(comment.getId(), user.getId());
            List<Response> responses = this.responseRepository.getResponsesByCommentId(comment.getId());
            commentGetDto.setNumberResponse(responses.size());
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
            }
            if(comment.isDeleted() == false) {
                comment.getUser().setComments(null);
                comment.getUser().setPassword(null);
                comment.getUser().setFollowers(null);
                comment.getUser().setFollowing(null);
                List<Response> responses = this.responseRepository.getResponsesByCommentId(comment.getId());
                CommentGetDto commentGetDto = new CommentGetDto();
                commentGetDto.setNumberResponse(responses.size());
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
        for (Comment comment : commentList) {
            comment.getUser().setComments(null);
            comment.getUser().setPassword(null);
            comment.getUser().setFollowers(null);
            comment.getUser().setFollowing(null);
        }
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
            Optional<Movie> currentMovie = movieRepository.findByTmdbId(comment1.getElement_id());
            String message = "Votre commentaire sur le film " + currentMovie.get().getName() + " a été signalé comme spoil";
            this.commentRepository.save(comment1);
            Notification notification = new Notification();
            notification.setMessage(message);
            notification.setType("comment");
            notification.setSeverity("info");
            userService.notificationToUser(comment1.getUser().getUsername(),notification);
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
        List<Response> responses = this.responseRepository.getResponsesByCommentId(comment.get().getId());
        commentGetDto.setNumberResponse(responses.size());
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
        commentGetDto.getComments().getUser().setComments(null);
        commentGetDto.getComments().getUser().setPassword(null);
        commentGetDto.getComments().getUser().setFollowers(null);
        commentGetDto.getComments().getUser().setFollowing(null);

        return commentGetDto;
    }

    public Optional<UserSimpleDto> getUserByEmail(String email) {
        Optional<UserSimpleDto> user = userRepository.findSimpleUserByEmail(email);
        return user;
    }

    public boolean addResponseComment(UserSimpleDto userSimpleDto, ResponseCommentDto responseCommentDto) {
        try {
            User user = this.userRepository.findById(userSimpleDto.getId()).orElseThrow(() -> new IllegalStateException(UserNotFound));
            Optional<Comment> comment = this.commentRepository.findById(responseCommentDto.getCommentId());
            Response response = new Response();
            response.setUser(user);
            response.setContent(responseCommentDto.getText());
            response.setComment(comment.get());
            responseRepository.save(response);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Response> fetchResponseComment(Long commentId) {
        Optional<Comment> comment = this.commentRepository.findById(commentId);
        List<Response> responseList = this.responseRepository.findAllByComment(comment.get());
        for (Response response : responseList) {
            response.getUser().setComments(null);
            response.getUser().setPassword(null);
            response.getUser().setFollowers(null);
            response.getUser().setFollowing(null);
        }
        return responseList;
    }
}
