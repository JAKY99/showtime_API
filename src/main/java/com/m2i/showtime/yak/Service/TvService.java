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
import java.util.*;

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

    public Serie createSerieWithSeasonsAndEpisodes(Long tmbdId) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();

        String urlToCall =  "https://api.themoviedb.org/3/tv/" + tmbdId + "?api_key=" + TMDB_KEY;
        HttpRequest dataFromSerie = HttpRequest.newBuilder()
                .uri(new URI(urlToCall))
                .GET()
                .build();
        HttpResponse response = client.send(dataFromSerie, HttpResponse.BodyHandlers.ofString());
        System.out.println(response);

        JSONObject documentObj = new JSONObject(response.body().toString());
        Gson gson = new Gson();

        AddSerieDto serie = gson.fromJson(String.valueOf(documentObj) , AddSerieDto.class);
        AddSeasonDto[] seasons = serie.seasons;

        Set<Season> seasonList = new HashSet<>();
        for (int i = 0; i < seasons.length; i++) {

            String urlEpisode = "https://api.themoviedb.org/3/tv/" + tmbdId + "/season/" + seasons[i].season_number + "?api_key=" + TMDB_KEY;
            HttpRequest dataEpisode = HttpRequest.newBuilder()
                    .uri(new URI(urlEpisode))
                    .GET()
                    .build();
            HttpResponse resp = client.send(dataEpisode, HttpResponse.BodyHandlers.ofString());
            System.out.println(resp);

            JSONObject documentObj2 = new JSONObject(resp.body().toString());

            AddSeasonDto seasonDto = gson.fromJson(String.valueOf(documentObj2) , AddSeasonDto.class);

            Set<Episode> episodeSet = new HashSet<>();
            for (int j = 0; j < seasonDto.episodes.length; j++) {
                Episode newEpisode = new Episode(seasonDto.episodes[j].id, seasonDto.episodes[j].name);
                episodeSet.add(newEpisode);
            }

            Season season = new Season(seasons[i].season_number, seasons[i].id, seasons[i].name ,episodeSet);
            seasonList.add(season);
        }

        Serie newSerie = new Serie(tmbdId, serie.name, seasonList, Status.SEEN);
        tvRepository.save(newSerie);
        return newSerie;
    }

    public Serie getSerieOrCreateIfNotExist(Long tmdbId) throws IOException, URISyntaxException, InterruptedException {
        Optional<Serie> optionalSerie = tvRepository.findByTmdbId(tmdbId);
                System.out.println(optionalSerie);

        Serie serie = optionalSerie.orElse(null);

        if (serie == null) {

            Serie newSerie = this.createSerieWithSeasonsAndEpisodes(tmdbId);
            System.out.println("Serie" + newSerie.getName() + " created");
            return newSerie;
        }

        return tvRepository.findByTmdbId(tmdbId).get();
    }
}
