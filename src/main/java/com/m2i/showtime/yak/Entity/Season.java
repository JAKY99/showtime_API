package com.m2i.showtime.yak.Entity;

import lombok.AllArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "season")

public class Season {
    @Id
    private Long id;

    private String season_number;

    private Long tmdbSeasonId;

    private String name;

    private Boolean watched;
    public Season() {

    }
    @OneToMany
    private List<Episode> episodes;

    public Season(Long id, String season_number, Long tmdbSeasonId, String name, List<Episode> episodes, Boolean watched) {
        this.id = id;
        this.season_number = season_number;
        this.tmdbSeasonId = tmdbSeasonId;
        this.name = name;
        this.episodes = episodes;
        this.watched = watched;
    }

}