package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterDto {
    private String firstname;
    private String lastname;
    private String username;
    private String password;

}
