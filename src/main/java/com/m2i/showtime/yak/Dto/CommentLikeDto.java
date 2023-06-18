package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentLikeDto {

    private String userMail;
    private Long commentId;
    private int numberLikes;
    private boolean userLiked;
}
