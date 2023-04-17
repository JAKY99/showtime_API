package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.UsersWatchedSeason;
import com.m2i.showtime.yak.Entity.UsersWatchedSeasonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersWatchedSeasonRepository extends JpaRepository<UsersWatchedSeason, UsersWatchedSeasonId> {
    @Query("SELECT u FROM UsersWatchedSeason u  WHERE u.season.id = ?1 AND u.user.id = ?2")
    Optional<UsersWatchedSeason> findBySeasonIdAndUserId(Long  SeasonId, Long userId);

    @Query("SELECT u FROM UsersWatchedSeason u  WHERE u.season.tmdbSeasonId = ?1 AND u.user.id = ?2")
    Optional<UsersWatchedSeason> findByTmdbIdAndUserId(Long  tmdbSeasonId , Long userId);

}