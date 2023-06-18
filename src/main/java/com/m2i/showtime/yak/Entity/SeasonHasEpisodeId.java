package com.m2i.showtime.yak.Entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SeasonHasEpisodeId implements Serializable {
    private static final long serialVersionUID = 4154427906713960898L;
    @Column(name = "episode_id", nullable = false)
    private Long episodeId;

    @Column(name = "season_id", nullable = false)
    private Long seasonId;

    public Long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Long episodeId) {
        this.episodeId = episodeId;
    }

    public Long getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SeasonHasEpisodeId entity = (SeasonHasEpisodeId) o;
        return Objects.equals(this.seasonId, entity.seasonId) &&
                Objects.equals(this.episodeId, entity.episodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seasonId, episodeId);
    }

}