package com.m2i.showtime.yak.Dto;

import com.m2i.showtime.yak.Entity.Permission;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
public class RoleManagerDto {
    private Long id;
    private String role;
    private String display_name;
    private String description;

    private String permissions;

}
