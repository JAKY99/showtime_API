package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentNotifDto {

    String username;
    Long comment_id;
    String message;
    String topicName;
}
