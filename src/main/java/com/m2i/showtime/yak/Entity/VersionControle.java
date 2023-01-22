package com.m2i.showtime.yak.Entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
@Entity
@Getter
@Setter
@NoArgsConstructor
public class VersionControle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private LocalDateTime version;
    private String type;

    public VersionControle(LocalDateTime version, String type) {
        this.version = version;
        this.type = type;
    }
}
