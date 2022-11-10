package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Service.CustomThreadService;
import com.m2i.showtime.yak.Service.MovieService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/movie")

public class MovieController {
    @Value("${application.elasticurl}")
    private String elasticbaseUrl;
    @Value("${application.imdb.apiKey}")
    private String apiKey;
    public final MovieRepository movieRepo;
    public final MovieService movieService;

    public MovieController(MovieRepository movieRepo, MovieService movieService) {
        this.movieRepo = movieRepo;
        this.movieService = movieService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public List<Movie> getMovies(){
        return movieService.getMovies();
    }

    @PostMapping
    public void insertNewMovie(@RequestBody Movie movie){
        movieService.addNewMovie(movie);
    }

    @PutMapping("/{movieId}/user/{userId}")
    Movie addUserToMovie(
            @PathVariable("movieId") Long movieId,
            @PathVariable("userId") Long userId
    ) {
        return movieService.addUserToMovie(movieId, userId);
    }

    @PostMapping("/insert/bulk/elasticsearch")
    public boolean insertMovieBulkElastic(@RequestBody InsertMovieBulkElasticDto InsertMovieBulkElastic) throws Exception {;
      return movieService.insertMovieBulkElastic(InsertMovieBulkElastic,elasticbaseUrl,apiKey);
    }

}
