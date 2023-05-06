package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialFollowingRequestDto {
    private String usernameRequested;
    private String usernameRequester;
}
