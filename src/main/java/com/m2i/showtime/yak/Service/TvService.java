package com.m2i.showtime.yak.Service;

import com.google.gson.Gson;
import com.m2i.showtime.yak.Configuration.RedisConfig;
import com.m2i.showtime.yak.Dto.AddEpisodeDto;
import com.m2i.showtime.yak.Dto.AddSeasonDto;
import com.m2i.showtime.yak.Dto.AddSerieDto;
import com.m2i.showtime.yak.Dto.increaseDurationSerieDto;
import com.m2i.showtime.yak.Entity.Episode;
import com.m2i.showtime.yak.Entity.EpisodeRepository;
import com.m2i.showtime.yak.Entity.Season;
import com.m2i.showtime.yak.Entity.Serie;
import com.m2i.showtime.yak.Repository.TvRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TvService {

    @Value("${external.service.imdb.apiKey}")

    private String TMDB_KEY;
    private final UserRepository userRepository;
    private final RedisConfig redisConfig;
    private final TvRepository tvRepository;
    private final RedisService redisService;
    private final EpisodeRepository episodeRepository;

    public TvService(UserRepository userRepository, RedisConfig redisConfig, TvRepository tvRepository, RedisService redisService,
                     EpisodeRepository episodeRepository) {
        this.userRepository = userRepository;
        this.redisConfig = redisConfig;
        this.tvRepository = tvRepository;
        this.redisService = redisService;
        this.episodeRepository = episodeRepository;
    }



    public List<Serie> getSeries() {
        return tvRepository.findAll();
    }



    public AddSeasonDto[] getTmdbSeasonsInfos(Long tmdbId) throws URISyntaxException, IOException, InterruptedException {
        AddSerieDto serie = getSerieDetails(tmdbId);


        return serie.seasons;
    }

    public AddSerieDto getSerieDetails(Long tmdbId) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("tmdb key :"+TMDB_KEY);
        String urlToCall =  "https://api.themoviedb.org/3/tv/" + tmdbId + "?api_key=" + TMDB_KEY;
        System.out.println("urlToCall :"+urlToCall);
        JSONObject documentObj = this.redisService.getDataFromRedisForInternalRequest(urlToCall);
        Gson gson = new Gson();
        System.out.println("response :"+documentObj.toString());
        return gson.fromJson(String.valueOf(documentObj) , AddSerieDto.class);
    }

    public Serie createSerieWithSeasonsAndEpisodes2(Long tmbdId) throws IOException, InterruptedException, URISyntaxException {
        Gson gson = new Gson();

        AddSerieDto serie = getSerieDetails(tmbdId);
        AddSeasonDto[] seasons = serie.seasons;

        Set<Season> seasonList = new HashSet<>();
        for (int i = 0; i < seasons.length; i++) {
            String urlEpisode = "https://api.themoviedb.org/3/tv/" + tmbdId + "/season/" + seasons[i].season_number + "?api_key=" + TMDB_KEY;
            JSONObject documentObj2 = this.redisService.getDataFromRedisForInternalRequest(urlEpisode);

            AddSeasonDto seasonDto = gson.fromJson(String.valueOf(documentObj2) , AddSeasonDto.class);
            if(seasonDto.air_date != null||!seasonDto.episodes[0].air_date.equals("null")) {

                LocalDate today = LocalDate.now();
                LocalDate date = LocalDate.parse(seasonDto.episodes[0].air_date);

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

    public Serie createSerieWithSeasonsAndEpisodes(Long tmbdId) throws IOException, InterruptedException, URISyntaxException {
        Gson gson = new Gson();

        AddSerieDto serie = getSerieDetails(tmbdId);
        AddSeasonDto[] seasons = serie.seasons;

        Set<Season> seasonList = Collections.newSetFromMap(new ConcurrentHashMap<>());

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < seasons.length; i++) {
            long seasonNumber = seasons[i].season_number;
            String urlEpisode = "https://api.themoviedb.org/3/tv/" + tmbdId + "/season/" + seasonNumber + "?api_key=" + TMDB_KEY;

            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                JSONObject documentObj2 = null;
                try {
                    documentObj2 = this.redisService.getDataFromRedisForInternalRequest(urlEpisode);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                AddSeasonDto seasonDto = gson.fromJson(String.valueOf(documentObj2), AddSeasonDto.class);

                    boolean canBeAdd = false;
                    LocalDate today = LocalDate.now();
                    try{
                        if(seasonDto.episodes[0].air_date != null) {
                            LocalDate date = LocalDate.parse(seasonDto.episodes[0].air_date);
                            canBeAdd = date.isBefore(today);
                        }
                        if(seasonDto.air_date != null) {
                            LocalDate date = LocalDate.parse(seasonDto.air_date);
                            canBeAdd = date.isBefore(today);
                        }
                        if(seasonDto.air_date == null) {
                            canBeAdd = true;
                        }
                    }catch (Exception e){
                        System.out.println("error :"+e.getMessage());
                    }


                    if (canBeAdd) {
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

                        Season season = new Season(seasonNumber, seasons[finalI].id, seasons[finalI].name, episodeSet);
                        seasonList.add(season);
                    }

            }, executorService);

            futures.add(future);
        }

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Shutdown the executor service
        executorService.shutdown();

        Serie newSerie = new Serie(tmbdId, serie.name, seasonList);
        tvRepository.save(newSerie);
        return newSerie;
    }


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

    public void createSeasonIfNotExist(increaseDurationSerieDto increaseDurationSerieDto) throws URISyntaxException, IOException, InterruptedException {
        Optional<Season> season = tvRepository.findByTmdbId(increaseDurationSerieDto.getTvTmdbId()).get().getHasSeason().stream().filter(s -> s.getSeason_number() == increaseDurationSerieDto.getSeasonNumber()).findFirst();
        if(season.isPresent()){
            return;
        }
        String urlEpisode = "https://api.themoviedb.org/3/tv/" + increaseDurationSerieDto.getTvTmdbId() + "/season/" + increaseDurationSerieDto.getEpisodeNumber() + "?api_key=" + TMDB_KEY;
        JSONObject documentObj2 = this.redisService.getDataFromRedisForInternalRequest(urlEpisode);
        Gson gson = new Gson();
        AddSeasonDto seasonDto = gson.fromJson(String.valueOf(documentObj2) , AddSeasonDto.class);
        if(seasonDto.air_date != null||!seasonDto.episodes[0].air_date.equals("null")) {

            LocalDate today = LocalDate.now();
            LocalDate date = LocalDate.parse(seasonDto.episodes[0].air_date);

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
                try{
                    Season seasonToADD = new Season(seasonDto.season_number, seasonDto.id, seasonDto.name, episodeSet);
                    Optional<Serie> tvshow = tvRepository.findByTmdbId(increaseDurationSerieDto.getTvTmdbId());
                    if(!tvshow.get().getHasSeason().contains(seasonToADD)){
                        tvshow.get().getHasSeason().add(seasonToADD);
                    }
                    tvRepository.save(tvshow.get());
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }

            }
        }


    }
    public void createEpisodeIfNotExist(increaseDurationSerieDto increaseDurationSerieDto) throws URISyntaxException, IOException, InterruptedException {
        createSeasonIfNotExist(increaseDurationSerieDto);
        Optional<Episode> episode = episodeRepository.findByTmdbEpisodeId(increaseDurationSerieDto.getTvTmdbId());
        if(episode.isPresent()){
            return;
        }

        String urlToCall =  "https://api.themoviedb.org/3/tv/" + increaseDurationSerieDto.getTvTmdbId() + "/season/"+ increaseDurationSerieDto.getSeasonNumber()+"/episode/" +  increaseDurationSerieDto.getEpisodeNumber() + "?api_key=" + this.TMDB_KEY;

        JSONObject checkInCache = this.redisService.getDataFromRedisForInternalRequest(urlToCall);
        Gson gson = new Gson();
        AddEpisodeDto result_search = gson.fromJson(String.valueOf(checkInCache), AddEpisodeDto.class);
        LocalDate today = LocalDate.now();
        LocalDate dateOnAir = LocalDate.parse(result_search.air_date);
        Episode newEpisode = new Episode(
                result_search.id,
                result_search.name,
                result_search.season_number,
                result_search.episode_number
        );

        try{
            episodeRepository.save(newEpisode);
            Optional<Serie> serie = tvRepository.findByTmdbId(increaseDurationSerieDto.getTvTmdbId());

            boolean check = serie
                    .get()
                    .getHasSeason()
                    .stream()
                    .filter(season -> season.getSeason_number() == increaseDurationSerieDto.getSeasonNumber())
                    .findFirst()
                    .get()
                    .getHasEpisode().contains(newEpisode);
            if(check){
                return;
            }
            serie
                    .get()
                    .getHasSeason()
                    .stream()
                    .filter(season -> season.getSeason_number() == increaseDurationSerieDto.getSeasonNumber())
                    .findFirst()
                    .get()
                    .getHasEpisode()
                    .add(newEpisode);
            tvRepository.save(serie.get());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }






    }
}
