package com.m2i.showtime.yak.Dto;

import lombok.Getter;

@Getter
public class KafkaMessageDto {
    private String message;

    private String topicName;
}
