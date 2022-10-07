package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final UserRepository userRepository;

    private final MovieRepository movieRepository;

    public MovieService(UserRepository userRepository, MovieRepository movieRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    public List<Movie> getMovies() {
        return movieRepository.findAll();
    }

    public Movie addNewMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie addUserToMovie(Long movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId)
                                     .get();
        User user = userRepository.findById(userId)
                                  .get();

        return movieRepository.save(movie);
    }

    public Movie getMovieOrCreateIfNotExist(Long movieId, String movieName) {
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        Movie movie = optionalMovie.orElse(null);

        if (movie == null) {
            Movie newMovie = new Movie(movieId, movieName);
            movieRepository.save(newMovie);
            return newMovie;
        }

        return movie;
    }
}
