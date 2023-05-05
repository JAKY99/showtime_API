package com.m2i.showtime.yak.Service;

import com.google.gson.Gson;
import com.m2i.showtime.yak.Configuration.RedisConfig;
import com.m2i.showtime.yak.Dto.AddSeasonDto;
import com.m2i.showtime.yak.Dto.AddSerieDto;
import com.m2i.showtime.yak.Entity.Episode;
import com.m2i.showtime.yak.Entity.Season;
import com.m2i.showtime.yak.Entity.Serie;
import com.m2i.showtime.yak.Repository.TvRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Future;

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

    @Value("${application.imdb.apiKey}")

    private String TMDB_KEY;

    public List<Serie> getSeries() {
        return tvRepository.findAll();
    }



    public AddSeasonDto[] getTmdbSeasonsInfos(Long tmdbId) throws URISyntaxException, IOException, InterruptedException {
        AddSerieDto serie = getSerieDetails(tmdbId);


        return serie.seasons;
    }

    public AddSerieDto getSerieDetails(Long tmdbId) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String urlToCall =  "https://api.themoviedb.org/3/tv/" + tmdbId + "?api_key=" + TMDB_KEY;
        HttpRequest dataFromSerie = HttpRequest.newBuilder()
                .uri(new URI(urlToCall))
                .GET()
                .build();
        HttpResponse response = client.send(dataFromSerie, HttpResponse.BodyHandlers.ofString());

        JSONObject documentObj = new JSONObject(response.body().toString());
        Gson gson = new Gson();

        return gson.fromJson(String.valueOf(documentObj) , AddSerieDto.class);
    }

    public Serie createSerieWithSeasonsAndEpisodes(Long tmbdId) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        AddSerieDto serie = getSerieDetails(tmbdId);
        AddSeasonDto[] seasons = serie.seasons;

        Set<Season> seasonList = new HashSet<>();
        for (int i = 0; i < seasons.length; i++) {
            String urlEpisode = "https://api.themoviedb.org/3/tv/" + tmbdId + "/season/" + seasons[i].season_number + "?api_key=" + TMDB_KEY;
            HttpRequest dataEpisode = HttpRequest.newBuilder()
                    .uri(new URI(urlEpisode))
                    .GET()
                    .build();
            HttpResponse resp = client.send(dataEpisode, HttpResponse.BodyHandlers.ofString());

            JSONObject documentObj2 = new JSONObject(resp.body().toString());

            AddSeasonDto seasonDto = gson.fromJson(String.valueOf(documentObj2) , AddSeasonDto.class);
            if(seasonDto.air_date != null) {

                LocalDate today = LocalDate.now();
                LocalDate date = LocalDate.parse(seasonDto.air_date);

                if (!date.isAfter(today)) {

                    Set<Episode> episodeSet = new HashSet<>();
                    for (int j = 0; j < seasonDto.episodes.length; j++) {
                        Episode newEpisode = new Episode(
                                seasonDto.episodes[j].id,
                                seasonDto.episodes[j].name,
                                seasonDto.episodes[j].season_number,
                                seasonDto.episodes[j].episode_number
                        );
                        episodeSet.add(newEpisode);
                    }

                    Season season = new Season(seasons[i].season_number, seasons[i].id, seasons[i].name, episodeSet);
                    seasonList.add(season);

                }
            }
        }

        Serie newSerie = new Serie(tmbdId, serie.name, seasonList);
        tvRepository.save(newSerie);
        return newSerie;
    }

//    public boolean AddSerieToWatchedSeries(){
//
//    };


    public Serie getSerieOrCreateIfNotExist(Long tmdbId) throws IOException, URISyntaxException, InterruptedException {
        Optional<Serie> optionalSerie = tvRepository.findByTmdbId(tmdbId);

        Serie serie = optionalSerie.orElse(null);

        if (serie == null) {
            Serie newSerie = this.createSerieWithSeasonsAndEpisodes(tmdbId);
            System.out.println("Serie " + newSerie.getName() + " created");
            return newSerie ;
        }

        return tvRepository.findByTmdbId(tmdbId).get();
    }
    
}
