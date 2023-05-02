package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    @Query("SELECT u FROM Serie u WHERE u.tmdbId = ?1")
    Optional<Serie> findSerieByTmdbId(Long tmdbId);

//    @Query("SELECT u FROM UsersWatchedSeries u WHERE u.serie.hasSeason = ?1")
//    Optional<Serie> userWatchedEpisodes(Serie serie);
}