package com.m2i.showtime.yak.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "users_watched_series")
@Getter
@Setter
@NoArgsConstructor
public class UsersWatchedSeries {
    @EmbeddedId
    private UsersWatchedSeriesId id;

    @MapsId("serieId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UsersWatchedSeriesId getId() {
        return id;
    }

    public void setId(UsersWatchedSeriesId id) {
        this.id = id;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    @Column(name = "watched_number")
    private Long watchedNumber;

}