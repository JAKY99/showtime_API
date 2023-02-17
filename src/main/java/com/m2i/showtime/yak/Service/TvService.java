package com.m2i.showtime.yak.Service;

import com.google.gson.Gson;
import com.m2i.showtime.yak.Configuration.RedisConfig;
import com.m2i.showtime.yak.Dto.AddEpisodeDto;
import com.m2i.showtime.yak.Dto.AddSeasonDto;
import com.m2i.showtime.yak.Dto.AddSerieDto;
import com.m2i.showtime.yak.Entity.Episode;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.Season;
import com.m2i.showtime.yak.Entity.Serie;
import com.m2i.showtime.yak.Enum.Status;
import com.m2i.showtime.yak.Repository.TvRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
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
//    @Value("${TMDB_BASE_URL}")
//    private String TMDB_BASE_URL;

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

    public Serie createSerieWithSeasonsAndEpisodes(Long tmbdId) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();

        String urlToCall =  "https://api.themoviedb.org/3/tv/" + tmbdId + "?api_key=" + TMDB_KEY;
        HttpRequest dataFromSerie = HttpRequest.newBuilder()
                .uri(new URI(urlToCall))
                .GET()
                .build();
        HttpResponse response = client.send(dataFromSerie, HttpResponse.BodyHandlers.ofString());

        JSONObject documentObj = new JSONObject(response.body().toString());
        Gson gson = new Gson();

        AddSerieDto serie = gson.fromJson(String.valueOf(documentObj) , AddSerieDto.class);
        AddSeasonDto[] seasons = serie.seasons;

        ArrayList<Season> seazons = new ArrayList<>();
        for (int i = 0; i < seasons.length; i++) {
            System.out.println(seasons[i].id);
//            /tv/{tv_id}/season/{season_number}/episode/{episode_number}
            ArrayList<Episode> episodes = new ArrayList<>();

            Long episodeCount = seasons[i].episode_count;
            for(int j = 1; j < episodeCount; j++) {
                String urlEpisode = "https://api.themoviedb.org/3/tv/" + tmbdId + "/season/" + seasons[i].season_number + "/episode/" + j + "?api_key=" + TMDB_KEY;
                HttpRequest dataEpisode = HttpRequest.newBuilder()
                        .uri(new URI(urlEpisode))
                        .GET()
                        .build();
                HttpResponse resp = client.send(dataEpisode, HttpResponse.BodyHandlers.ofString());
                System.out.println(resp);
                JSONObject documentObj2 = new JSONObject(resp.body().toString());

                AddEpisodeDto episode = gson.fromJson(String.valueOf(documentObj2) , AddEpisodeDto.class);

                Episode newEpisode = new Episode(episode.id, episode.name);
                episodes.add(newEpisode);

            }
//            Season season = new Season(seasons[i].season_number, seasons[i].id, seasons[i].name, episodes);
//            seazons.add(season);
        }

//        Serie newSerie = new Serie(tmbdId, serie.name, seazons, Status.SEEN);
//        tvRepository.save(newSerie);
//        return newSerie;

        return null;
    }

    public Serie getSerieOrCreateIfNotExist(Long tmdbId) throws IOException, URISyntaxException, InterruptedException {
        Optional<Serie> optionalSerie = tvRepository.findByTmdbId(tmdbId);
                System.out.println(optionalSerie);

        Serie serie = optionalSerie.orElse(null);

        if (serie == null) {

            Serie newSerie = this.createSerieWithSeasonsAndEpisodes(tmdbId);
            System.out.println(newSerie);
            return newSerie;
        }

        return tvRepository.findByTmdbId(tmdbId).get();
    }
}
