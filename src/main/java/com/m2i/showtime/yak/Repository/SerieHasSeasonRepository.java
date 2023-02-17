package com.m2i.showtime.yak.Repository;

import com.m2i.showtime.yak.Entity.SerieHasSeason;
import com.m2i.showtime.yak.Entity.SerieHasSeasonId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerieHasSeasonRepository extends JpaRepository<SerieHasSeason, SerieHasSeasonId> {
}