package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VersionControlDto {
    private String version;

    public VersionControlDto(String version) {
        this.version = version;
    }
}
