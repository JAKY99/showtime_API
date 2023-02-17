package com.m2i.showtime.yak.Entity;

import javax.persistence.*;

@Entity
@Table(name = "serie_has_seasons")
public class SerieHasSeason {
    @EmbeddedId
    private SerieHasSeasonId id;

    @MapsId("seasonId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @MapsId("serieId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    public SerieHasSeasonId getId() {
        return id;
    }

    public void setId(SerieHasSeasonId id) {
        this.id = id;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }
    @Column(name = "watched_number")
    private Long watchedNumber;
}