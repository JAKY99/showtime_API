package com.m2i.showtime.yak.Dto;

import lombok.Getter;

@Getter

public class ResetPasswordUseDto {

    private String email;
    private String password;
    private String token;
}
