package com.m2i.showtime.yak.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/tv")

public class SerieController {
    @Value("${application.elasticurl}")
    private String elasticbaseUrl;
    @Value("${application.imdb.apiKey}")
    private String apiKey;

    //TvRepo

    //TvService


}