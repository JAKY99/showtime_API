package com.m2i.showtime.yak.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "serie_has_seasons")
@Getter
@Setter
@NoArgsConstructor
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

    @Column(name = "watched_number")
    private Long watchedNumber;

    @Column(name = "user_id")
    private Long userId;

//    @MapsId("userId")
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
}