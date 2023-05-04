package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.movie_id = ?1 and c.isSpoiler = false and c.isValidate = true and c.isDeleted = false order by c.datePublication desc")
    Comment[] getCommentsByMovieId(long movieId);

    @Query("SELECT c FROM Comment c WHERE c.movie_id = ?1 and c.user.id = ?2 and c.isValidate = false and c.isDeleted = false")
    Comment[] getUserCommentsByMovieIdAndUserId(long movieId, long id);
}
