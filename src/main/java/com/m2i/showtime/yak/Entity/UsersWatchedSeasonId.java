package com.m2i.showtime.yak.Entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UsersWatchedSeasonId implements Serializable {
    private static final long serialVersionUID = 6992233704242713607L;
    @Column(name = "season_id", nullable = false)
    private Long seasonId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Long getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(Long seasonId) {
        this.seasonId = seasonId;
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
        UsersWatchedSeasonId entity = (UsersWatchedSeasonId) o;
        return Objects.equals(this.seasonId, entity.seasonId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seasonId, userId);
    }

}