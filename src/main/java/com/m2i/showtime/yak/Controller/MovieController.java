package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Service.MovieService;
import com.m2i.showtime.yak.Service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/movie")

public class MovieController {
    @Value("${application.elasticurl}")
    private String elasticbaseUrl;
    @Value("${application.imdb.apiKey}")
    private String apiKey;
    public final MovieRepository movieRepo;
    public final MovieService movieService;
    @Autowired
    public final UserService userService;

    public MovieController(MovieRepository movieRepo, MovieService movieService, UserService userService) {
        this.movieRepo = movieRepo;
        this.movieService = movieService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public List<Movie> getMovies() {
        return movieService.getMovies();
    }

    @PostMapping
    public void insertNewMovie(@RequestBody Movie movie) {
        movieService.addNewMovie(movie);
    }

    @PutMapping("/{movieId}/user/{userId}")
    Movie addUserToMovie(
            @PathVariable("movieId") Long movieId, @PathVariable("userId") Long userId) {
        return movieService.addUserToMovie(movieId, userId);
    }

    @GetMapping("/recommended-for-user")
    public SearchRecommendedMovieAPIDto getRecommendedMoviesForUser(
            Authentication authentication) throws URISyntaxException, IOException, InterruptedException {
        Optional<UserSimpleDto> userSimpleDto = userService.getUserByEmail(authentication.getPrincipal()
                                                                                         .toString());
        long idUser = userSimpleDto.orElseThrow(() -> {
                                       throw new IllegalStateException("User not found.");
                                   })
                                   .getId();
        return movieService.getRecommendedMoviesForUser(idUser);
    }

    @PostMapping("/insert/bulk/elasticsearch")
    public boolean insertMovieBulkElastic(
            @RequestBody InsertMovieBulkElasticDto InsertMovieBulkElastic) throws Exception {
        return movieService.insertMovieBulkElastic(InsertMovieBulkElastic, elasticbaseUrl, apiKey);
    }

    @PostMapping("/redis/getcache")
    public SearchMovieAPIDto getRedisMovieCache(@RequestBody RedisCacheDto redisCacheDto) throws Exception {
        return movieService.getRedisMovieCache(redisCacheDto);
    }

}
