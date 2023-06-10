package com.m2i.showtime.yak.Entity;

import com.m2i.showtime.yak.common.trophy.TrophyType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "trophy")
public class Trophy {
    @Id
    @SequenceGenerator(
            name = "trophy_sequence",
            sequenceName = "trophy_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "trophy_sequence"
    )
    private Long id;
    private String name;
    private String description;
    private String image;
    @Enumerated(EnumType.STRING)
    private TrophyType type;
    private LocalDateTime dateCreated = LocalDateTime.now();



    public Trophy() {

    }
    public Trophy(String name, String description, String image, TrophyType type) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.type = type;
    }
}