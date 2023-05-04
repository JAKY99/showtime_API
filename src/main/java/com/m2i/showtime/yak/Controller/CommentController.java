package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.CommentNotifDto;
import com.m2i.showtime.yak.Dto.userCommentDto;
import com.m2i.showtime.yak.Entity.Comment;
import com.m2i.showtime.yak.Service.CommentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<Comment> getComments(@PathVariable("movieId") int movieId) {
        return commentService.getComments(movieId);
    }

    @GetMapping("/getUserComments/{movieId}")
    public List<Comment> getComments(@PathVariable("movieId") int movieId, @RequestHeader("Authorization") String token) {
        return commentService.getUserComments(movieId, token);
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
}
