package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class userCommentDto {
    int movieId;
    String commentText;
    String userMail;
}
