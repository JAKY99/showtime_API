package com.m2i.showtime.yak.Entity;

import com.m2i.showtime.yak.Enum.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "users_watched_seasons")
@Getter
@Setter
@NoArgsConstructor
public class UsersWatchedSeason {
    @EmbeddedId
    private UsersWatchedSeasonId id;

    @MapsId("seasonId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "watched_number" , columnDefinition = "int default 1")
    private Long watchedNumber;

    @Column(name = "status" , columnDefinition = "varchar(255) default 1")
    private Status status;
}