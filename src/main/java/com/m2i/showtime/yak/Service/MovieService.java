package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
public class MovieService {

    private final UserRepository userRepository;

    private final MovieRepository movieRepository;
    
    public MovieService(UserRepository userRepository, MovieRepository movieRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    public List<Movie> getMovies(){
        return movieRepository.findAll();
    }

    public void addNewMovie(Movie movie) {

        movieRepository.save(movie);
    }

    public Movie addUserToMovie(Long movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId).get();
        User user = userRepository.findById(userId).get();

        return movieRepository.save(movie);
    }
}
