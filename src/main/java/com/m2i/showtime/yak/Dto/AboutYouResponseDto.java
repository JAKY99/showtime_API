package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AboutYouResponseDto {
    private String aboutYou;
    public AboutYouResponseDto() {
    }
    public AboutYouResponseDto(String aboutYou) {
        this.aboutYou = aboutYou;
    }
}
