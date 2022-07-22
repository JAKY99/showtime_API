package com.m2i.showtime.yak.Jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UsernameAndPasswordAuthenticationRequest {
    private String Username;
    private String password;
}
