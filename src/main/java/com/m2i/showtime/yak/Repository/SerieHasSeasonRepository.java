package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieHasSeasonRepository extends JpaRepository<SerieHasSeason, SerieHasSeasonId> {
    @Query("SELECT m FROM SerieHasSeason m WHERE m.serie.tmdbId = ?1")
    List<SerieHasSeason> findAllRelatedSeason(Long serieId);


}