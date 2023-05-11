package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class userCommentDto {
    long movieId;
    String movieTitle;
    String commentText;
    String userMail;
}
