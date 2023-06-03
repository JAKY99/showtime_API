package com.m2i.showtime.yak.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Metrics {
    @Id
    @SequenceGenerator(
            name = "metric_sequence",
            sequenceName = "metric_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "metric_sequence"
    )
    private Long id;
    private Long totalUsers;
    private Long totalConnectedUsers;
    private Date dateCreated = new Date();
}
