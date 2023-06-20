package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Episode;
import com.m2i.showtime.yak.Entity.UsersWatchedEpisode;
import com.m2i.showtime.yak.Entity.UsersWatchedSeason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    @Query("SELECT e FROM Episode e WHERE e.imbd_id = ?1")
    Optional<Episode> findByTmdbEpisodeId(Long seasonTmdbId);

}