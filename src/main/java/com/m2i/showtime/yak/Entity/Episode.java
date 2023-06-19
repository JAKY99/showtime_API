package com.m2i.showtime.yak.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "episode",
        indexes = @Index(columnList = "imbd_id , season_number , episode_number"),
        uniqueConstraints = {
                @UniqueConstraint(name = "episode_imbd_id_unique", columnNames = "imbd_id")
        }
)
@Getter
@Setter
public class Episode {
    @Id
    @SequenceGenerator(
            name = "episode_sequence",
            sequenceName = "episode_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "episode_sequence"
    )
    private Long id;


    private Long imbd_id;

    private String name;

    private Long season_number;

    private Long episode_number;

    private int runtime;
    public String air_date;


    public Episode() {
    }
    public Episode(Long imbd_id,String name, Long season_number, Long episode_number) {
        this.imbd_id = imbd_id;
        this.name = name;
        this.season_number = season_number;
        this.episode_number = episode_number;

    }

    public Episode(Long imbd_id, String name, Long seasonNumber, Long episodeNumber, String airDate, int runtime) {
        this.imbd_id = imbd_id;
        this.name = name;
        this.season_number = seasonNumber;
        this.episode_number = episodeNumber;
        this.air_date = airDate;
        this.runtime = runtime;
    }
}