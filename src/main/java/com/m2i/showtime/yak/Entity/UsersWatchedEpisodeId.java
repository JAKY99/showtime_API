package com.m2i.showtime.yak.Entity;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UsersWatchedEpisodeId implements Serializable {
    private static final long serialVersionUID = 1349270215621828939L;
    @Column(name = "episode_id", nullable = false)
    private Long episodeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Long episodeId) {
        this.episodeId = episodeId;
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
        UsersWatchedEpisodeId entity = (UsersWatchedEpisodeId) o;
        return Objects.equals(this.episodeId, entity.episodeId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(episodeId, userId);
    }

}