package com.m2i.showtime.yak.Service;

import com.google.gson.Gson;
import com.m2i.showtime.yak.Configuration.RedisConfig;
import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.Actor;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
public class MovieService {

    @Value("${application.imdb.apiKey}")
    private String apiKey;

    private final UserRepository userRepository;

    private final MovieRepository movieRepository;
    private final RedisConfig redisConfig;

    private final ActorService actorService;

    public MovieService(
            UserRepository userRepository, MovieRepository movieRepository, RedisConfig redisConfig,
            ActorService actorService) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.redisConfig = redisConfig;
        this.actorService = actorService;
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

        return movieRepository.findByTmdbId(tmdbId)
                              .get();
    }

    public Boolean insertMovieBulkElastic(
            InsertMovieBulkElasticDto InsertMovieBulkElasticDto, String elasticbaseUrl,
            String apiKey) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        //        int[] listIdsFavUser = {663712,436270,675054,49046,420634};
        int[] listIdsFavUser = InsertMovieBulkElasticDto.getMovieIds();
        String urlMovieUserElastic = "user-" + InsertMovieBulkElasticDto.getUserId() + "-movie";
        List<TheMovieDbApiKeywordsMovieDto> keyWordsList = new ArrayList<>();
        String FilteredkeyWordsList = "";

        for (int i = 0; i < listIdsFavUser.length; i++) {
            String urlToCall = "https://api.themoviedb.org/3/movie/" + listIdsFavUser[i] + "/keywords?api_key=" + apiKey;
            HttpRequest getKeywordsFromCurrentFavMovieRequest = HttpRequest.newBuilder()
                                                                           .uri(new URI(urlToCall))
                                                                           .GET()
                                                                           .build();
            HttpResponse response = client.send(
                    getKeywordsFromCurrentFavMovieRequest, HttpResponse.BodyHandlers.ofString());
            JSONObject documentObj = new JSONObject(response.body()
                                                            .toString());
            Gson gson = new Gson();
            SearchKeywordsMovieApiDto result_search = gson.fromJson(
                    String.valueOf(documentObj), SearchKeywordsMovieApiDto.class);
            for (int j = 0; j < result_search.keywords.length; j++) {
                keyWordsList.add(result_search.keywords[j]);
            }
        }
        List<String> result = new ArrayList<>();
        for (TheMovieDbApiKeywordsMovieDto x : keyWordsList) {
            result.add(String.valueOf(x.getId()));
        }
        for (int i = 0; i < result.size(); i++) {
            int finalI = i;
            int currentCountKeyWord = result.stream()
                                            .filter(x -> x.equals(result.get(finalI)))
                                            .toArray().length;
            if (currentCountKeyWord > 1) {
                if (FilteredkeyWordsList.length() > 0) {
                    FilteredkeyWordsList = FilteredkeyWordsList + "|" + result.get(i);
                }
                if (FilteredkeyWordsList.length() == 0) {
                    FilteredkeyWordsList = result.get(i);
                }
                result.removeAll(Arrays.asList(result.stream()
                                                     .filter(x -> x.equals(result.get(finalI)))
                                                     .toArray()));
            }
        }

        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(new URI(
                                                 "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=en-US&page=1&with_keywords=" + URLEncoder.encode(
                                                         FilteredkeyWordsList, "UTF-8")))
                                         .GET()
                                         .build();

        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject documentObj = new JSONObject(response.body()
                                                        .toString());
        Gson gson = new Gson();
        SearchMovieAPIDto result_search = gson.fromJson(String.valueOf(documentObj), SearchMovieAPIDto.class);
        int totalPage = result_search.total_pages <= 500 ? result_search.total_pages : 500;
        int page = 0;

        HttpRequest clearIndex = HttpRequest.newBuilder()
                                            .uri(new URI(elasticbaseUrl + "/" + urlMovieUserElastic))
                                            .DELETE()
                                            .build();
        HttpResponse clearIndexResponse = client.send(clearIndex, HttpResponse.BodyHandlers.ofString());

        for (int i = 0; i < totalPage; i++) {
            page = i + 1;

            String currentUrl = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=en-US&page=" + page + "&with_keywords=" + URLEncoder.encode(
                    FilteredkeyWordsList, "UTF-8");
            RunInsertBulkDto currentBulkDto = new RunInsertBulkDto(elasticbaseUrl, currentUrl, page,
                                                                   urlMovieUserElastic);
            CustomThreadService thread = new CustomThreadService(currentBulkDto, "runInsertBulk");
            thread.start();
        }
        return true;
    }

    public SearchMovieAPIDto getRedisMovieCache(
            RedisCacheDto redisCacheDto) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        boolean check = redisConfig.jedis()
                                   .get(redisCacheDto.getUrlApi()) != null;
        String resultToShow;
        if (check) {
            resultToShow = redisConfig.jedis()
                                      .get(redisCacheDto.getUrlApi());
            for (int i = 0; i < resultToShow.length() - 5; i++) {
                if (resultToShow.charAt(i) == '}') {
                    resultToShow = new StringBuilder(resultToShow).insert(i + 1, ",")
                                                                  .toString();
                }
            }
            JSONObject resultToSend = new JSONObject("{\"page\":1,\"results\":" + resultToShow + "}");
            Gson gson = new Gson();
            SearchMovieAPIDto result_search = gson.fromJson(resultToSend.toString(), SearchMovieAPIDto.class);
            return result_search;
        }
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(new URI(redisCacheDto.getUrlApi()))
                                         .GET()
                                         .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject documentObj = new JSONObject(response.body()
                                                        .toString());
        JSONArray results = documentObj.getJSONArray("results");
        RunInsertRedisCacheDto runInfo = new RunInsertRedisCacheDto(redisConfig, redisCacheDto.getUrlApi());
        CustomThreadService thread = new CustomThreadService(runInfo, "runInsertRedisCache");
        thread.start();
        Gson gson = new Gson();
        final String[][] resultToSend = {{new String()}};
        results.forEach(movie -> {
            JSONObject currentMovie = new JSONObject(movie.toString());
            String baseUrl = "https://image.tmdb.org/t/p/";
            String sizePoster = "w500";
            String sizeBackdrop = "original";
            String posterPath = baseUrl + sizePoster + currentMovie.getString("poster_path");
            String backdropPath = baseUrl + sizeBackdrop + currentMovie.getString("backdrop_path");
            currentMovie.put("poster_path", posterPath);
            currentMovie.put("backdrop_path", backdropPath);
            resultToSend[0][0] += currentMovie;
            for (int i = 0; i < resultToSend[0][0].length() - 5; i++) {
                if (resultToSend[0][0].charAt(i) == '}') {
                    resultToSend[0] = new String[]{new StringBuilder(Arrays.toString(resultToSend[0])).insert(
                            i + 1, ",").toString()};
                }
            }
        });

        JSONObject resultToSendObj = new JSONObject("{\"page\":1,\"results\":" + resultToSend[0][0] + "}");
        SearchMovieAPIDto result_search = gson.fromJson(resultToSendObj.toString(), SearchMovieAPIDto.class);
        return result_search;

    }

    public SearchRecommendedMovieAPIDto getRecommendedMoviesForUser(
            long idUser) throws URISyntaxException, IOException, InterruptedException {
        String urlToCall = "https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey + "&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=";
        SearchRecommendedMovieAPIDto resultSearch = new SearchRecommendedMovieAPIDto();

        for (int i = 1; i < 5; i++) {
            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(new URI(urlToCall + i))
                                             .GET()
                                             .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject documentObj = new JSONObject(response.body()
                                                            .toString());
            JSONArray results = documentObj.getJSONArray("results");
            Gson gson = new Gson();
            JSONObject resultToSendObj = new JSONObject("{\"page\":" + i + ",\"results\":" + results + "}");
            var resultTemp = gson.fromJson(resultToSendObj.toString(), SearchRecommendedMovieAPIDto.class);
            if (resultSearch.results != null) {
                resultSearch.results.addAll(resultTemp.results);
            } else {
                resultSearch.results = resultTemp.results;
            }
        }

        //REMOVE MOVIES ALREADY SEEN BY THE USER
        Optional<User> userOptional = userRepository.findById(idUser);
        User user = userOptional.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });

        user.getWatchedMovies()
            .forEach(userMovie -> {
                TheMovieDbApiMovieDto theMovieDbApiMovieDto = resultSearch.results.stream()
                                                                                  .filter(x -> x.getId() == userMovie.getTmdbId())
                                                                                  .findFirst()
                                                                                  .orElseThrow(() -> {
                                                                                      throw new IllegalStateException(
                                                                                              "Unable to filter resultSearch list while removing already seen movies.");
                                                                                  });

                resultSearch.results.remove(theMovieDbApiMovieDto);
            });

        //REMOVE MOVIES EXCLUDED ACTORS
        Set<Actor> excludedActorIdFromRecommended = user.getExcludedActorIdFromRecommended();
        ArrayList<TheMovieDbApiMovieDto> tempList = new ArrayList<>(resultSearch.results);
        tempList.forEach(movie -> {
            try {
                SearchCast movieCast = actorService.getMovieCast(movie.getId());
                excludedActorIdFromRecommended.forEach(actor -> {
                    if (movieCast.results.stream()
                                         .anyMatch(actorCastDto -> actorCastDto.getId() == actor.getTmdbId())) {
                        resultSearch.results.remove(movie);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

        resultSearch.results = new ArrayList<>(resultSearch.results.subList(0, 20));
        return resultSearch;
    }
}
