package com.m2i.showtime.yak.common.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum CommentType {
    SERIE("serie"),
    MOVIE("movie");

    private final String type;

    CommentType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
}
