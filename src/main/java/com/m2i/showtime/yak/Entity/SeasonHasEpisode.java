package com.m2i.showtime.yak.Entity;

import javax.persistence.*;

@Entity
@Table(name = "season_has_episodes")
public class SeasonHasEpisode {
    @EmbeddedId
    private SeasonHasEpisodeId id;

    @MapsId("episodeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @MapsId("seasonId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    public SeasonHasEpisodeId getId() {
        return id;
    }

    public void setId(SeasonHasEpisodeId id) {
        this.id = id;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    @Column(name = "watched_number")
    private Long watchedNumber;

}