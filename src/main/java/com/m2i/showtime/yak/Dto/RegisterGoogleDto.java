package com.m2i.showtime.yak.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterGoogleDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
}
