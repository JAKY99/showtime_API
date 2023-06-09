package com.m2i.showtime.yak.Dto.Search;

import com.m2i.showtime.yak.Entity.Comment;
import com.m2i.showtime.yak.Entity.Response;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommentGetDto {
    private Comment comments;

    private boolean isLiked = false;

    private int numberResponse;

    private String elementName;

}
