package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("SELECT l FROM Like l WHERE l.userId = ?1 and l.commentId = ?2")
    Like findbyUserIdAndCommentId(Long userId, Long commentId);

    @Query("SELECT l FROM Like l WHERE l.commentId = ?1 and l.userId = ?2")
    Optional<Like> getLikeByCommentIdAndUserId(Long id, Long id1);
}
