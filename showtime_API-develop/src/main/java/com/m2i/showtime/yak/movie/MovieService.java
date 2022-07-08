package com.m2i.showtime.yak.movie;

import com.m2i.showtime.yak.user.User;
import com.m2i.showtime.yak.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
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
