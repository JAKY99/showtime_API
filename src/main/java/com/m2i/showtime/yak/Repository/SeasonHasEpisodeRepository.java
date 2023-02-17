package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.SeasonHasEpisode;
import com.m2i.showtime.yak.Entity.SeasonHasEpisodeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeasonHasEpisodeRepository extends JpaRepository<SeasonHasEpisode, SeasonHasEpisodeId> {
}