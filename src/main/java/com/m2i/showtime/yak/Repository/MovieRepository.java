package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
