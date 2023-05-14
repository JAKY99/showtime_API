package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;
import org.h2.util.json.JSONArray;

@Getter
@Setter
public class EditRoleManagerRequestDto {
    private Long id;
    private String role;
    private String display_name;
    private String description;
    private String permissions;
}
