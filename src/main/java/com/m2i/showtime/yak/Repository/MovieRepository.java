package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT m FROM Movie m WHERE m.tmdbId = ?1")
    Optional<Movie> findByTmdbId(Long tmdbId);

}
