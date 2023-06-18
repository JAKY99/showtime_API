package com.m2i.showtime.yak.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
@NoArgsConstructor
public class RunInsertBulkDto {
    String elasticbaseUrl;
    String currentUrl;
    int page;
    String urlMovieUserElastic;

    public RunInsertBulkDto(String elasticbaseUrl, String currentUrl, int page, String urlMovieUserElastic) {
        this.elasticbaseUrl = elasticbaseUrl;
        this.currentUrl = currentUrl;
        this.page = page;
        this.urlMovieUserElastic = urlMovieUserElastic;
    }


}
