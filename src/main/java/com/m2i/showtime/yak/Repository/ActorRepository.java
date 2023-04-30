package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.Actor;
import com.m2i.showtime.yak.Entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    @Query("SELECT a FROM Actor a WHERE a.TmdbId = ?1")
    Optional<Actor> findByTmdbId(Long tmdbId);
}
