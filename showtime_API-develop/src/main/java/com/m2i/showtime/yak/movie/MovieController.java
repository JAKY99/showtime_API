package com.m2i.showtime.yak.movie;

import com.m2i.showtime.yak.user.User;
import org.springframework.web.bind.annotation.*;

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
