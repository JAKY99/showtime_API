package com.m2i.showtime.yak.Entity;

import com.m2i.showtime.yak.Enum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "serie")
public class Serie {
    @Id
    @SequenceGenerator(
            name = "serie_sequence",
            sequenceName = "serie_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "serie_sequence"
    )
    private Long id;

    private Long tmdbId;

    private String name;

    public Serie() {
    }

    @ManyToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinTable(name = "serie_has_seasons",
            joinColumns = {
                    @JoinColumn(name = "serie_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "season_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<Season> hasSeason = new HashSet<>();


    public Serie(Long tmdbId, String name, Set<Season> hasSeason ) {
        this.tmdbId = tmdbId;
        this.name = name;
        this.hasSeason = hasSeason;
    }
}