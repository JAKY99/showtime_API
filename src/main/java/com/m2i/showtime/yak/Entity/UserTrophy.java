package com.m2i.showtime.yak.Entity;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "user_trophys")
public class UserTrophy {
    @EmbeddedId
    private UserTrophyId id;

    @MapsId("trophyId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trophy_id", nullable = false)
    private Trophy trophy;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime dateCreated = LocalDateTime.now();

}