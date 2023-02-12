package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Configuration.RedisConfig;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.Serie;
import com.m2i.showtime.yak.Repository.TvRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

@Service
public class TvService {
    private final UserRepository userRepository;
    private final RedisConfig redisConfig;
    private final TvRepository tvRepository;

    public TvService(UserRepository userRepository, RedisConfig redisConfig, TvRepository tvRepository) {
        this.userRepository = userRepository;
        this.redisConfig = redisConfig;
        this.tvRepository = tvRepository;
    }
    @Value("${TMDB_BASE_URL}")
    private String TMDB_BASE_URL;

    @Value("${TMDB_KEY}")
    private String TMDB_KEY;

    public List<Serie> getSeries() {
        return tvRepository.findAll();
    }


//    public Movie getMovieOrCreateIfNotExist(Long tmdbId, String movieName) {
//        Optional<Movie> optionalMovie = movieRepository.findByTmdbId(tmdbId);
//        Movie movie = optionalMovie.orElse(null);
//
//        if (movie == null) {
//            Movie newMovie = new Movie(tmdbId, movieName);
//            movieRepository.save(newMovie);
//            return newMovie;
//        }
//
//        return movieRepository.findByTmdbId(tmdbId).get();
//    }

    public Serie createSerieWithSeasonsAndEpisodes(Long tmbdId){
        // call a l'api tmdb pour récup toutes les données relatives à la série

//        String urlTvDetails = "curl -X GET"+TMDB_BASE_URL+"tv/"+tmbdId+"?api_key="+TMDB_KEY+"&language=en-US";
//        RestTemplate restTemplate = new RestTemplate();
//        Object[] tvDetails = restTemplate.getForObject(urlTvDetails, Object[].class);


//        // faire comme ça + init HttpClient
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(new URI("https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=en-US&page=1&with_keywords="+ URLEncoder.encode(FilteredkeyWordsList, "UTF-8")))
//                .GET()
//                .build();
//
//    // print in command
//        System.out.println(tvDetails);
//
//
//        Serie Serie = new Serie();
//        return Serie;

        return null;
    }

    public Serie getSerieOrCreateIfNotExist(Long tmdbId, String serieName) {
        Optional<Serie> optionalSerie = tvRepository.findByTmdbId(tmdbId);
        Serie serie = optionalSerie.orElse(null);

        if (serie == null) {

            Serie newSerie = this.createSerieWithSeasonsAndEpisodes(tmdbId);
            return newSerie;
        }

        return tvRepository.findByTmdbId(tmdbId).get();
    }
}
