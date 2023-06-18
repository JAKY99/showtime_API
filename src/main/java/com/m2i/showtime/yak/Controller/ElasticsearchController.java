package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Service.ElasticsearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("api/v1/elasticsearch")
public class ElasticsearchController {
    private final ElasticsearchService elasticSearchService;

    public ElasticsearchController(ElasticsearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @GetMapping("/update")
    public void dailyUpdate() throws IOException, URISyntaxException, InterruptedException {
        elasticSearchService.dailyUpdate();
    }
}
