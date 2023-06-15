package com.m2i.showtime.yak.Entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    @Query("SELECT e FROM Episode e WHERE e.imbd_id = ?1")
    Optional<Episode> findByTmdbEpisodeId(Long seasonTmdbId);
}