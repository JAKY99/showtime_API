package com.m2i.showtime.yak.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "season_has_episodes")
@Getter
@Setter
@NoArgsConstructor
public class SeasonHasEpisode {
    @EmbeddedId
    private SeasonHasEpisodeId id;

    @MapsId("episodeId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @MapsId("seasonId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;


//    @Column(name = "watched_number")
//    private Long watchedNumber;

//    @MapsId("userId")
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
}