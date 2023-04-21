package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class SocialSearchResponseDto {
    private String username;
    private String fullName;
    private int score;
    private String profilePicture;

}
