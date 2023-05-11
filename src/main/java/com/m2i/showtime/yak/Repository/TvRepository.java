package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TvRepository extends JpaRepository<Serie, Long> {
    @Query("SELECT m FROM Serie m WHERE m.tmdbId = ?1")
    Optional<Serie> findByTmdbId(Long tmdbId);

}
