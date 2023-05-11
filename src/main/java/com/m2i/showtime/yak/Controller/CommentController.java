package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.CommentLikeDto;
import com.m2i.showtime.yak.Dto.CommentNotifDto;
import com.m2i.showtime.yak.Dto.Search.CommentGetDto;
import com.m2i.showtime.yak.Dto.UserSimpleDto;
import com.m2i.showtime.yak.Dto.userCommentDto;
import com.m2i.showtime.yak.Entity.Comment;
import com.m2i.showtime.yak.Service.CommentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
@RequestMapping(path = "api/v1/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/saveComment")
    public boolean saveComment(@RequestBody userCommentDto userCommentDto) {
        commentService.saveComment(userCommentDto);
        return true;
    }

    @GetMapping("/getComments/{movieId}")
    public List<CommentGetDto> getComments(Authentication authentication, @PathVariable("movieId") int movieId) {
        Optional<UserSimpleDto> userSimpleDto = commentService.getUserByEmail(authentication.getPrincipal()
                .toString());
        return commentService.getComments(movieId, userSimpleDto.orElseThrow(() -> {
                    throw new IllegalStateException("User not found.");
                }));
    }

    @GetMapping("/getUserComments/{movieId}")
    public List<CommentGetDto> getUserComments(Authentication authentication, @PathVariable("movieId") int movieId) {
        Optional<UserSimpleDto> userSimpleDto = commentService.getUserByEmail(authentication.getPrincipal()
                .toString());
        return commentService.getUserComments(movieId, userSimpleDto.orElseThrow(() -> {
                    throw new IllegalStateException("User not found.");
                }));
    }

    @GetMapping("/getAllComments")
    public List<Comment> getComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/comment/{id}")
    public List<Comment> getCommentById(@PathVariable("id") int id) {
        return commentService.getcommentbyId(id);
    }

    @PutMapping("/validateComment")
    public boolean validateComment(@RequestBody Long commentId) {
        return commentService.validateComment(commentId);
    }

    @PutMapping("/rejectComment")
    public boolean rejectComment(@RequestBody Long commentId) {
        return commentService.rejectComment(commentId);
    }

    @PutMapping("/spoilComment")
    public boolean spoilComment(@RequestBody CommentNotifDto commentNotifDto) {
        return commentService.spoilComment(commentNotifDto);
    }

    @PostMapping("/likeComment")
    public CommentGetDto likeComment(Authentication authentication, @RequestBody CommentLikeDto commentLikeDto) {
        Optional<UserSimpleDto> userSimpleDto = commentService.getUserByEmail(authentication.getPrincipal()
                .toString());
        return commentService.likeComment(userSimpleDto.orElseThrow(() -> {
                    throw new IllegalStateException("User not found.");
                }), commentLikeDto);
    }
}
