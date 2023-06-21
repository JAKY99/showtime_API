package com.m2i.showtime.yak.Entity;

import com.m2i.showtime.yak.Enum.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "users_watched_series")
@Getter
@Setter
@NoArgsConstructor
public class UsersWatchedSeries {

    public UsersWatchedSeries(Long  SerieId, Long userId) {
        this.watchedNumber = 0L;
        this.status = Status.WATCHING;
    }
    @EmbeddedId
    private UsersWatchedSeriesId id;

    @MapsId("serieId")
    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.REMOVE, CascadeType.REFRESH},fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "watched_number" , columnDefinition = "int default 1")
    private Long watchedNumber;

    @Column(name = "status" , columnDefinition = "varchar(255) default 1")
    private Status status;

    @Column(name = "createdOn", columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private Instant lastUpdatedOn;

}