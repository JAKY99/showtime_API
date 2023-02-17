package com.m2i.showtime.yak.Entity;

import lombok.AllArgsConstructor;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "season")

public class Season {
    @Id
    @SequenceGenerator(
            name = "season_sequence",
            sequenceName = "season_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "season_sequence"
    )
    private Long id;

    private Long season_number;

    private Long tmdbSeasonId;

    private String name;
    public Season() {

    }
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "season_has_episodes",
            joinColumns = {
                    @JoinColumn(name = "season_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "episode_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<Episode> hasEpisode = new HashSet<>();

    public Season(Long season_number, Long tmdbSeasonId, String name, Set<Episode> hasEpisode) {
        this.season_number = season_number;
        this.tmdbSeasonId = tmdbSeasonId;
        this.name = name;
        this.hasEpisode = hasEpisode;
    }

}