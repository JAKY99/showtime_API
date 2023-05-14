package com.m2i.showtime.yak.Dto;

import com.m2i.showtime.yak.Entity.Response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseCommentDto {
    private Long commentId;
    private String text;
}
