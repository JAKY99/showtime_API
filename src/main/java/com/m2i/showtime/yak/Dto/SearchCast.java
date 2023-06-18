package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class SearchCast {
    public ArrayList<ActorCastDto> results;
}
