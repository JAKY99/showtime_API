package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KafkaResponseDto {
    private String responseMessage;
    public KafkaResponseDto(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}

