package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Episode;
import com.m2i.showtime.yak.Entity.UsersWatchedEpisode;
import com.m2i.showtime.yak.Entity.UsersWatchedEpisodeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersWatchedEpisodeRepository extends JpaRepository<UsersWatchedEpisode, UsersWatchedEpisodeId> {
    @Query("SELECT u FROM UsersWatchedEpisode u  WHERE u.episode.id = ?1 AND u.user.id = ?2")
    Optional<UsersWatchedEpisode> findByEpisodeIdAndUserId(Long imbd_id, Long id);

    @Query("SELECT u FROM UsersWatchedEpisode u  WHERE u.episode.imbd_id = ?1 AND u.user.id = ?2")
    Optional<UsersWatchedEpisode> findByEpisodeImdbIdAndUserId(Long tmdbID, Long id);

    @Query("SELECT u FROM Episode u  WHERE u.imbd_id = ?1")
    Optional<Episode> findEpisodeByTmdbId(Long tmdbId);

    @Query("SELECT u FROM Episode u INNER JOIN UsersWatchedEpisode s ON u.id = s.episode.id WHERE s.user.username = ?1 AND u.id = ?2")
    Optional<Episode> isEpisodeWatchedByUser(String username, Long episodeId);
}