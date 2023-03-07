package com.m2i.showtime.yak.Entity;

import com.m2i.showtime.yak.Enum.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "users_watched_episodes")
@Getter
@Setter
@NoArgsConstructor
public class UsersWatchedEpisode {
    @EmbeddedId
    private UsersWatchedEpisodeId id;

    @MapsId("episodeId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "watched_number")
    private Long watchedNumber;

    @Column(name = "status")
    private Status status;

    public UsersWatchedEpisodeId getId() {
        return id;
    }

    public void setId(UsersWatchedEpisodeId id) {
        this.id = id;
    }

    public Episode getEpisode() {
        return episode;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}