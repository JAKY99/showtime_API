package com.m2i.showtime.yak.Dto;

import com.m2i.showtime.yak.Entity.Role;
import lombok.Getter;

@Getter
public class UpdateUserDto {
    long id;
    private String firstName;
    private String lastName;
    private String country;
    private Role role;

}
