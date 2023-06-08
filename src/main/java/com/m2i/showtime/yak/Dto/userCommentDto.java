package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class userCommentDto {
    long elementId;
    String elementTitle;
    String commentText;
    String userMail;
    String typeElement;
}
