package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    @Query("SELECT g FROM Genre g WHERE g.TmdbId = ?1")
    Optional<Genre> findByTmdbId(Long tmdbId);
}
