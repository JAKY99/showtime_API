package com.m2i.showtime.yak.Entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SerieHasSeasonId implements Serializable {
    private static final long serialVersionUID = 4975626078224494294L;
    @Column(name = "season_id", nullable = false)
    private Long seasonId;

    @Column(name = "serie_id", nullable = false)
    private Long serieId;

    public Long getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
    }

    public Long getSerieId() {
        return serieId;
    }

    public void setSerieId(Long serieId) {
        this.serieId = serieId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SerieHasSeasonId entity = (SerieHasSeasonId) o;
        return Objects.equals(this.seasonId, entity.seasonId) &&
                Objects.equals(this.serieId, entity.serieId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seasonId, serieId);
    }

}