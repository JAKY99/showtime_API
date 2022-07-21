package com.m2i.showtime.yak.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Permission {

    @Id
    @SequenceGenerator(
            name = "permission_sequence",
            sequenceName = "permission_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "permission_sequence"
    )
    private Long id;

    @Column(unique = true)
    private String permission;

    private String display_name;
    private String description;

    public Permission(String permission) {
        this.permission = permission;
    }

    public Permission(String permission,
                      String display_name,
                      String description) {
        this.permission = permission;
        this.display_name = display_name;
        this.description = description;
    }
}
