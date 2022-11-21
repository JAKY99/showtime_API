package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Entity.UsersWatchedMovie;
import com.m2i.showtime.yak.Entity.UsersWatchedMovieId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersWatchedMovieRepository extends JpaRepository<UsersWatchedMovie, UsersWatchedMovieId> {
    @Query("SELECT u FROM UsersWatchedMovie u  WHERE u.movie.id = ?1 AND u.user.id = ?2")
    Optional<UsersWatchedMovie> findByMovieAndUserId(Long  movieId, Long userId);

    @Query("DELETE FROM UsersWatchedMovie u WHERE u.movie = ?1 AND u.user = ?2")
    Optional<UsersWatchedMovie> deleteByMovieAndUserId(Long  movieId, Long userId);
}