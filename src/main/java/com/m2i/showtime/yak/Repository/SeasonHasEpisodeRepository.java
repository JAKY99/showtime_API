package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.SeasonHasEpisode;
import com.m2i.showtime.yak.Entity.SeasonHasEpisodeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeasonHasEpisodeRepository extends JpaRepository<SeasonHasEpisode, SeasonHasEpisodeId> {

    @Query("SELECT s FROM SeasonHasEpisode s WHERE s.season.tmdbSeasonId = ?1")
    List<SeasonHasEpisode> findBySeasonImdbId(Long seasonImdbId);

//    @Query("SELECT s FROM SeasonHasEpisode s WHERE s.season.tmdbSeasonId = ?1")
//    Optional<SeasonHasEpisode> findBySeasonImdbId(Long seasonImdbId);
}