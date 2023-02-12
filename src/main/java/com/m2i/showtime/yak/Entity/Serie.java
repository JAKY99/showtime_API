package com.m2i.showtime.yak.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "serie")
public class Serie {
    @Id
    private Long id;

    private Long tmdbId;

    private String name;

    private String status;

    public Serie() {
    }
    @OneToMany
    private List<Season> seasons;
    public Serie(Long tmdbId, String name, List<Season> seasons,String status ) {
        this.tmdbId = tmdbId;
        this.name = name;
        this.seasons = seasons;
        this.status = status;
    }
}