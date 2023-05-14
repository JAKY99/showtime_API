package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Comment;
import com.m2i.showtime.yak.Entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {

    @Query("SELECT r FROM Response r WHERE r.comment = ?1")
    List<Response> findAllByComment(Comment comment);

    @Query("SELECT r FROM Response r WHERE r.comment.id = ?1")
    List<Response> getResponsesByCommentId(Long id);
}
