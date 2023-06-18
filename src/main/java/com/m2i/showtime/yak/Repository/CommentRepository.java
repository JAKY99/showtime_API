package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Comment;
import com.m2i.showtime.yak.common.comment.CommentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.element_id = ?1 and c.isSpoiler = false and c.isValidate = true and c.isDeleted = false order by c.datePublication desc")
    Optional<Comment[]> getCommentsByMovieId(long movieId);

    @Query("SELECT c FROM Comment c WHERE c.element_id = ?1 and c.user.id = ?2 and c.isValidate = false and c.isDeleted = false")
    Optional<Comment[]> getUserCommentsByMovieIdAndUserId(long movieId, long id);

    @Query("SELECT c FROM Comment c WHERE c.element_id = ?1 and c.typeElement =?2 and c.isSpoiler = false and c.isValidate = true and c.isDeleted = false order by c.datePublication desc")
    Optional<Comment[]> getCommentsByTypeAndId(long elementId , CommentType type);

    @Query("SELECT c FROM Comment c WHERE c.element_id = ?1 and c.user.id = ?2 and c.typeElement =?3 and c.isValidate = false and c.isDeleted = false")
    Optional<Comment[]> getUserCommentsByMovieIdAndUserIdAndType(long movieId, long id,CommentType type);
    @Query("SELECT c FROM Comment c WHERE c.user.username = ?1 and c.isSpoiler = false and c.isValidate = true and c.isDeleted = false order by c.datePublication desc")
    Optional<Comment[]> findByUsername(String username);
}
