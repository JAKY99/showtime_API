package com.m2i.showtime.yak.Entity;

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

    private String name;
    public Season() {

    }
    @OneToMany
    private List<Episode> episodes;
    public Season(Long id, String name, String season_number, List<Episode> episodes) {
        this.id = id;
        this.name = name;
        this.season_number = season_number;
        this.episodes= episodes;
    }
}