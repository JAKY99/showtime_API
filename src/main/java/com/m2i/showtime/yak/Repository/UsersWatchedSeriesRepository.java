package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.UsersWatchedSeries;
import com.m2i.showtime.yak.Entity.UsersWatchedSeriesId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersWatchedSeriesRepository extends JpaRepository<UsersWatchedSeries, UsersWatchedSeriesId> {
}