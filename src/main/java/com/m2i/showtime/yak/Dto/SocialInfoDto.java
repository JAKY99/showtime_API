package com.m2i.showtime.yak.Dto;

import com.m2i.showtime.yak.Entity.Comment;
import com.m2i.showtime.yak.Entity.Trophy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class SocialInfoDto {
    private String comments;
    private String about;
    private Set<Trophy> trophies;
}
