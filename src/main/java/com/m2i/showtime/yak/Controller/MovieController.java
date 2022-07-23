package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Service.MovieService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/movie")
public class MovieController {

    public final MovieService movieService;

    public MovieController(MovieService movieService) {
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

}