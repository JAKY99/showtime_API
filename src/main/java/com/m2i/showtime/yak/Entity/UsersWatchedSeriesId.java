package com.m2i.showtime.yak.Entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UsersWatchedSeriesId implements Serializable {
    private static final long serialVersionUID = 5963644084871647123L;
    @Column(name = "serie_id", nullable = false)
    private Long serieId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Long getSerieId() {
        return serieId;
    }

    public void setSerieId(Long serieId) {
        this.serieId = serieId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UsersWatchedSeriesId entity = (UsersWatchedSeriesId) o;
        return Objects.equals(this.serieId, entity.serieId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serieId, userId);
    }

}