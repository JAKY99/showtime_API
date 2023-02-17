package com.m2i.showtime.yak.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "episode")
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


    public Episode() {
    }
    public Episode(Long imbd_id,String name) {
        this.imbd_id = imbd_id;
        this.name = name;

    }
}