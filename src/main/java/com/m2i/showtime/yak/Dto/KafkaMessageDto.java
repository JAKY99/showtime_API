package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KafkaMessageDto {
    private String message;

    private String topicName;

    public KafkaMessageDto(String message, String topicName) {
        this.message = message;
        this.topicName = topicName;
    }
}
