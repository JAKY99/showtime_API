package com.m2i.showtime.yak.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "episode")
public class Episode {
    @Id
    private Long id;

    private Long imbd_id;

    private String name;

    private Boolean watched;

    public Episode() {
    }
    public Episode(Long id, Long imbd_id,String name,Boolean watched) {
        this.id = id;
        this.imbd_id = imbd_id;
        this.name = name;
        this.watched = watched;
    }
}