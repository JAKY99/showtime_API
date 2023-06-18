package com.m2i.showtime.yak.Entity;

import com.m2i.showtime.yak.Enum.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

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

    @Column(name = "watched_number" , columnDefinition = "int default 1")
    private Long watchedNumber;

    @Column(name = "createdOn", columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDateTime createdOn;

    @UpdateTimestamp
    private Instant lastUpdatedOn;

}