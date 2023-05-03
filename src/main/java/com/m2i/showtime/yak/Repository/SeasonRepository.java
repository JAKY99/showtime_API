package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Season;
import com.m2i.showtime.yak.Entity.SeasonHasEpisode;
import com.m2i.showtime.yak.Entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SeasonRepository extends JpaRepository<Season, Long> {
    @Query("SELECT m FROM Season m WHERE m.tmdbSeasonId = ?1")
    Optional<Season> findByTmdbSeasonId(Long tmdbId);

    @Query("SELECT m FROM SeasonHasEpisode m WHERE m.episode.imbd_id = ?1")
    Optional<SeasonHasEpisode> findSeasonWithEpisodeTmdbId(Long episodeTmdbId);

}
