package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.UsersWatchedMovie;
import com.m2i.showtime.yak.Entity.UsersWatchedSeries;
import com.m2i.showtime.yak.Entity.UsersWatchedSeriesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersWatchedSeriesRepository extends JpaRepository<UsersWatchedSeries, UsersWatchedSeriesId> {

    @Query("SELECT u FROM UsersWatchedSeries u  WHERE u.serie.id = ?1 AND u.user.username = ?2")
    Optional<UsersWatchedSeries> findBySerieAndUserMail(Long  SerieId, String userMail);

    @Query("SELECT u FROM UsersWatchedSeries u  WHERE u.serie.tmdbId = ?1 AND u.user.username = ?2")
    Optional<UsersWatchedSeries> findByImdbIdAndUserMail (Long ImdbId , String userMail);

    @Query("SELECT u FROM UsersWatchedSeries u WHERE u.serie.id = ?1 AND u.user.id = ?2")
    Optional<UsersWatchedSeries> findBySerieAndUserId(Long  SerieId, Long tmdbId);


}