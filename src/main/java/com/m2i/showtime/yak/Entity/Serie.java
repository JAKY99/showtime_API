package com.m2i.showtime.yak.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "serie")
public class Serie {
    @Id
    private Long id;

    private Long tmdbId;

    private String name;

    public Serie() {
    }
    @OneToMany
    private List<Season> seasons;
}