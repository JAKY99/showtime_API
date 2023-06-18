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

    @Query("SELECT u FROM UsersWatchedSeries u WHERE u.status = '1' AND u.user.username = ?1")
    Optional<UsersWatchedSeries[]> getWatchingSeries(String username);

    @Query("SELECT u FROM UsersWatchedSeries u WHERE u.status = '2' AND u.user.username = ?1")
    Optional<UsersWatchedSeries[]> getWatchedSeries(String username);

    @Query("SELECT u FROM UsersWatchedSeries u WHERE u.status = '2' AND u.user.username = ?1 ORDER BY u.lastUpdatedOn DESC")
    Optional<UsersWatchedSeries[]> getLastWatchedSeries(String username);


}