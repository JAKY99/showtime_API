package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Entity.Serie;
import com.m2i.showtime.yak.Repository.TvRepository;
import com.m2i.showtime.yak.Service.TvService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/tv")

public class SerieController {
    @Value("${application.elasticurl}")
    private String elasticbaseUrl;
    @Value("${application.imdb.apiKey}")
    private String apiKey;

    //TvRepo
    public final TvRepository tvRepo;
    public final TvService tvService;

    public SerieController(TvRepository tvRepo, TvService tvService) {
        this.tvRepo = tvRepo;
        this.tvService = tvService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public List<Serie> getSeries(){
        return tvService.getSeries();
    }


}