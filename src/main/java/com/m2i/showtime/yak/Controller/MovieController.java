package com.m2i.showtime.yak.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Service.MovieService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/movie")
public class MovieController {

    public final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
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

}
