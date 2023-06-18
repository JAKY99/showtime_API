package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseApiAgGridDto {
    private String severity;
    private String title;
    private String details;
    private boolean sticky;
}
