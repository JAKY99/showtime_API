package com.m2i.showtime.yak.Service;

import com.google.gson.Gson;
import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
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

    public Movie addUserToMovie(Long tmdbId, Long userId) {
        Movie movie = movieRepository.findById(tmdbId)
                                     .get();
        User user = userRepository.findById(userId)
                                  .get();

        return movieRepository.save(movie);
    }

    public Movie getMovieOrCreateIfNotExist(Long tmdbId, String movieName) {
        Optional<Movie> optionalMovie = movieRepository.findByTmdbId(tmdbId);
        Movie movie = optionalMovie.orElse(null);

        if (movie == null) {
            Movie newMovie = new Movie(tmdbId, movieName);
            movieRepository.save(newMovie);
            return newMovie;
        }

        return movie;
    }
    public Boolean insertMovieBulkElastic(InsertMovieBulkElasticDto InsertMovieBulkElasticDto,String elasticbaseUrl , String apiKey) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

//        int[] listIdsFavUser = {663712,436270,675054,49046,420634};
        int[] listIdsFavUser = InsertMovieBulkElasticDto.getMovieIds();
        String urlMovieUserElastic ="user-" + InsertMovieBulkElasticDto.getUserId() + "-movie";
        List<TheMovieDbApiKeywordsMovieDto> keyWordsList = new ArrayList<>();
        String FilteredkeyWordsList = "";

        for (int i = 0; i <listIdsFavUser.length ; i++) {
            String urlToCall =  "https://api.themoviedb.org/3/movie/" + listIdsFavUser[i] + "/keywords?api_key=" + apiKey;
            HttpRequest getKeywordsFromCurrentFavMovieRequest = HttpRequest.newBuilder()
                    .uri(new URI(urlToCall))
                    .GET()
                    .build();
            HttpResponse response = client.send(getKeywordsFromCurrentFavMovieRequest, HttpResponse.BodyHandlers.ofString());
            JSONObject documentObj = new JSONObject(response.body().toString());
            Gson gson = new Gson();
            SearchKeywordsMovieApiDto result_search = gson.fromJson(String.valueOf(documentObj), SearchKeywordsMovieApiDto.class);
            for (int j = 0; j < result_search.keywords.length ; j++) {
                keyWordsList.add(result_search.keywords[j]);
            }
        }
        List<String> result = new ArrayList<>();
        for (TheMovieDbApiKeywordsMovieDto x : keyWordsList) {
            result.add(String.valueOf(x.getId()));
        }
        for (int i = 0; i <result.size() ; i++) {
            int finalI = i;
            int currentCountKeyWord = result.stream().filter(x -> x.equals(result.get(finalI))).toArray().length;
            if(currentCountKeyWord>1){
                if(FilteredkeyWordsList.length()>0){
                    FilteredkeyWordsList = FilteredkeyWordsList + "|" + result.get(i);
                }
                if(FilteredkeyWordsList.length()==0){
                    FilteredkeyWordsList = result.get(i);
                }
                result.removeAll(Arrays.asList(result.stream().filter(x -> x.equals(result.get(finalI))).toArray()));
            }
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=en-US&page=1&with_keywords="+ URLEncoder.encode(FilteredkeyWordsList, "UTF-8")))
                .GET()
                .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject documentObj = new JSONObject(response.body().toString());
        Gson gson = new Gson();
        SearchMovieAPIDto result_search = gson.fromJson(String.valueOf(documentObj), SearchMovieAPIDto.class);
        int totalPage = result_search.total_pages<=500?result_search.total_pages:500;
        int page=0;

        HttpRequest clearIndex = HttpRequest.newBuilder()
                .uri(new URI(elasticbaseUrl + "/" + urlMovieUserElastic))
                .DELETE()
                .build();
        HttpResponse clearIndexResponse = client.send(clearIndex, HttpResponse.BodyHandlers.ofString());

        for (int i = 0; i < totalPage; i++) {
            page=i+1;

            String currentUrl = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=en-US&page=" + page + "&with_keywords="+ URLEncoder.encode(FilteredkeyWordsList, "UTF-8");
            RunInsertBulkDto currentBulkDto = new RunInsertBulkDto(elasticbaseUrl,currentUrl,page,urlMovieUserElastic);
            CustomThreadService thread = new CustomThreadService(currentBulkDto,"runInsertBulk");
            thread.start();
        }
        return true;
    }
}
