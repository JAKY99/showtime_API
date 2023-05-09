package com.m2i.showtime.yak.Service.User;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.m2i.showtime.yak.Configuration.HazelcastConfig;
import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.*;
import com.m2i.showtime.yak.Enum.Status;
import com.m2i.showtime.yak.Repository.*;
import com.m2i.showtime.yak.Service.LoggerService;
import com.m2i.showtime.yak.Service.MovieService;
import com.m2i.showtime.yak.Service.RedisService;
import com.m2i.showtime.yak.Service.TvService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@EnableAsync
public class UserService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    private final SerieRepository serieRepository;

    private final TvRepository tvRepository;
    private final MovieService movieService;
    private final TvService tvService;
    private final UsersWatchedMovieRepository usersWatchedMovieRepository;
    @Value("${application.bucketName}")
    private String bucketName;
    @Value("${application.awsAccessKey}")
    private String awsAccessKey;
    @Value("${application.awsSecretKey}")
    private String awsSecretKey;
    @Value("${application.awsSesAccessKey}")
    private String awsSesAccessKey;
    @Value("${application.awsSesSecretKey}")
    private String awsSesSecretKey;
    @Value("${application.imdb.apiKey}")
    private String apiKey;
    @Value("${spring.mail.resetPasswordUrl}")
    private String resetPasswordUrl;
    @Value("${spring.mail.mailOrigin}")
    private String mailOrigin;
    @Value("${spring.jwt.secretKey}")
    private String JWT_SECRET;
    private int multiplicatorTime = 1;
    private RedisService redisService;
    private HazelcastConfig hazelcastConfig;
    private final String UserNotFound="User not found";
    private final String tempPathName="/src/main/profile_pic_temp/original_";
    private final String basicErrorMessage="Something went wrong";
    private LoggerService LOGGER = new LoggerService();
    private final UsersWatchedSeriesRepository usersWatchedSeriesRepository;
    private final UsersWatchedEpisodeRepository usersWatchedEpisodeRepository;
    private final UsersWatchedSeasonRepository usersWatchedSeasonRepository;
    private final SeasonRepository seasonRepository;
    private final SeasonHasEpisodeRepository seasonHasEpisodeRepository;
    private final SerieHasSeasonRepository serieHasSeasonRepository;


    @Autowired
    public UserService(UserRepository userRepository, MovieRepository movieRepository, TvRepository tvRepository, MovieService movieService, TvService tvService, UsersWatchedMovieRepository usersWatchedMovieRepository, RedisService redisService, HazelcastConfig hazelcastConfig, LoggerService LOGGER,
                       UsersWatchedSeriesRepository usersWatchedSeriesRepository, UsersWatchedEpisodeRepository usersWatchedEpisodeRepository, UsersWatchedSeasonRepository usersWatchedSeasonRepository , SeasonRepository seasonRepository, SeasonHasEpisodeRepository seasonHasEpisodeRepository,
                       SerieHasSeasonRepository serieHasSeasonRepository , SerieRepository serieRepository){
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.tvService = tvService;
        this.tvRepository = tvRepository;
        this.movieService = movieService;
        this.usersWatchedMovieRepository = usersWatchedMovieRepository;
        this.redisService = redisService;
        this.hazelcastConfig = hazelcastConfig;
        this.LOGGER = LOGGER;
        this.usersWatchedSeriesRepository = usersWatchedSeriesRepository;
        this.usersWatchedEpisodeRepository = usersWatchedEpisodeRepository;
        this.usersWatchedSeasonRepository = usersWatchedSeasonRepository;
        this.seasonRepository = seasonRepository;
        this.seasonHasEpisodeRepository = seasonHasEpisodeRepository;
        this.serieHasSeasonRepository = serieHasSeasonRepository;
        this.serieRepository = serieRepository;
    }
    public Optional<UserSimpleDto> getUser(Long userId) {
        Optional<UserSimpleDto> user = userRepository.findSimpleUserById(userId);
        return user;
    }
    public Optional<UserSimpleDto> getUserByEmail(String email) {
        Optional<UserSimpleDto> user = userRepository.findSimpleUserByEmail(email);
        return user;
    }
    public User addUser(User user) {
        Optional<User> userOptional = userRepository.findUserByEmail(user.getUsername());
        if (userOptional.isPresent()){
            throw new IllegalStateException("email taken");
        }
        userRepository.save(user);
        return user;
    }
    public void deleteUser(Long userId) {

        if (!userRepository.existsById(userId)){
            throw new IllegalStateException("User does not exists");
        }
        userRepository.deleteById(userId);
    }
    @Transactional
    public void updateUser(Long userId,
                           User modifiedUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(("user with id "+ userId + "does not exists")));
        if (modifiedUser.getFirstName() != null &&
                modifiedUser.getFirstName().length() > 0 &&
                !Objects.equals(user.getFirstName(), modifiedUser.getFirstName())) {
            user.setFirstName(modifiedUser.getFirstName());
        }
        if (modifiedUser.getLastName() != null &&
                modifiedUser.getLastName().length() > 0 &&
                !Objects.equals(user.getLastName(), modifiedUser.getLastName())) {
            user.setLastName(modifiedUser.getLastName());
        }
        if (modifiedUser.getCountry() != null &&
                modifiedUser.getCountry().length() > 0 &&
                !Objects.equals(user.getCountry(), modifiedUser.getCountry())) {
            user.setCountry(modifiedUser.getCountry());
        }
        if (modifiedUser.getUsername() != null &&
                modifiedUser.getUsername().length() > 0 &&
                !Objects.equals(user.getUsername(), modifiedUser.getUsername())) {
            if (userRepository.findUserByEmail(modifiedUser.getUsername()).isPresent()){
                throw new IllegalStateException("email taken");
            }
            user.setUsername(modifiedUser.getUsername());
        }
    }
    public boolean isMovieInWatchlist(UserWatchedMovieDto userWatchedMovieDto) {
        Optional<UserSimpleDto> user = userRepository.isMovieWatched(
                userWatchedMovieDto.getUserMail(), userWatchedMovieDto.getTmdbId());
        return user.isEmpty() ? false : true;
    }
    public boolean addMovieInWatchlist(UserWatchedMovieAddDto userWatchedMovieAddDto) throws URISyntaxException, IOException, InterruptedException {
        Movie movie = movieService.getMovieOrCreateIfNotExist(userWatchedMovieAddDto.getTmdbId(),
                                                              userWatchedMovieAddDto.getMovieName());

        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        Long movieId = movieRepository.findByTmdbId(userWatchedMovieAddDto.getTmdbId()).orElseThrow(() -> new IllegalStateException(basicErrorMessage)).getId();
        Long userId = user.getId();
        Optional<UsersWatchedMovie> optionalUserWatchedMovie =  usersWatchedMovieRepository.findByMovieAndUserId(movieId,userId );
        if(!optionalUserWatchedMovie.isPresent()){
            user
            .getWatchedMovies()
            .add(movie);
            userRepository.save(user);
            this.increaseWatchedNumber(userWatchedMovieAddDto);
        }
        if(optionalUserWatchedMovie.isPresent()){
            Long currentWatchedNumber = optionalUserWatchedMovie.get().getWatchedNumber();
            optionalUserWatchedMovie.get().setWatchedNumber(currentWatchedNumber+1L);
            usersWatchedMovieRepository.save(optionalUserWatchedMovie.get());
        }
        this.increaseTotalMovieWatchedTime(userWatchedMovieAddDto);
        return true;
    }

    public boolean addSerieInWatchlist(UserWatchedSerieAddDto userWatchedSerieAddDto) throws URISyntaxException, IOException, InterruptedException {

        Serie serie = this.tvService.getSerieOrCreateIfNotExist(userWatchedSerieAddDto.getTmdbId());

        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedSerieAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));

        Long serieId = tvRepository.findByTmdbId(userWatchedSerieAddDto.getTmdbId()).orElseThrow(() -> new IllegalStateException(basicErrorMessage)).getId();
        Long userId = user.getId();
        Optional<UsersWatchedSeries> optionalUserWatchedSerie = usersWatchedSeriesRepository.findBySerieAndUserId(serieId, userId);
        if (!optionalUserWatchedSerie.isPresent()) {
            user
                    .getWatchedSeries()
                    .add(serie);
            userRepository.save(user);

            // change status serie
            Optional<UsersWatchedSeries> relatedSerie = usersWatchedSeriesRepository.findBySerieAndUserId(serieId, userId);
            relatedSerie.get().setStatus(Status.SEEN);

            this.increaseWatchedNumberSeries(userWatchedSerieAddDto);
        }

        // loop on all serie.getHasSeason()


        serie.getHasSeason().forEach(season -> {
            UserWatchedTvSeasonAddDto userWatchedTvSeasonAddDto = new UserWatchedTvSeasonAddDto(
                    userWatchedSerieAddDto.getUserMail(),
                    userWatchedSerieAddDto.getTmdbId(),
                    season.getTmdbSeasonId()
            );
            try {
                addSeasonInWatchlist(userWatchedTvSeasonAddDto);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });



        if (optionalUserWatchedSerie.isPresent()) {
            // increase watch nb
            Long currentWatchedNumber = optionalUserWatchedSerie.get().getWatchedNumber();
            optionalUserWatchedSerie.get().setWatchedNumber(currentWatchedNumber + 1L);

            usersWatchedSeriesRepository.save(optionalUserWatchedSerie.get());
        }

//        @TODO trouver un moyen de compter la durée d'une saison et l'ajouter
//        this.increaseTotalMovieWatchedTime(userWatchedSerieAddDto);

        return true;
    }

    public boolean addEpisodeInWatchlist(UserWatchedTvEpisodeAddDto userWatchedTvEpisodeAddDto ) throws URISyntaxException, IOException, InterruptedException {
        Serie serie = this.tvService.getSerieOrCreateIfNotExist(userWatchedTvEpisodeAddDto.getTvTmdbId());

        // récup l'user
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedTvEpisodeAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        Long userId = user.getId();

        // récup obj episode
        Optional<Episode> episode = usersWatchedEpisodeRepository.findEpisodeByTmdbId(userWatchedTvEpisodeAddDto.getEpisodeId());
        //Check episode is in watchedListEpisode
        Optional<UsersWatchedEpisode> optionalUserWatchedEpisode = usersWatchedEpisodeRepository.findByEpisodeIdAndUserId(episode.get().getId(), userId);

        // si pas présent alors add
        if (!optionalUserWatchedEpisode.isPresent()) {

            user
                    .getWatchedEpisodes()
                    .add(episode.get());
            userRepository.save(user);


            increaseWatchedNumberEpisode(userWatchedTvEpisodeAddDto);

        }else{
            Long currentWatchedNumber = optionalUserWatchedEpisode.get().getWatchedNumber();
            optionalUserWatchedEpisode.get().setWatchedNumber(currentWatchedNumber+1L);
            usersWatchedEpisodeRepository.save(optionalUserWatchedEpisode.get());

            increaseWatchedNumberEpisode(userWatchedTvEpisodeAddDto);
        }
        // méthode pour rajouter la saison si tout les épisodes d'une saison sont visionnés && mettre le bon statut
        if(userWatchedTvEpisodeAddDto.getTvSeasonid() != null){
            updateSeasonStatus(userWatchedTvEpisodeAddDto.getTvSeasonid(), userWatchedTvEpisodeAddDto.getUserMail(), userWatchedTvEpisodeAddDto.getTvTmdbId());
        }else{
            // query pour récup la saison id
            Optional<SeasonHasEpisode> seasonHasEpisode = seasonRepository.findSeasonWithEpisodeTmdbId(userWatchedTvEpisodeAddDto.getEpisodeId());
            updateSeasonStatus(seasonHasEpisode.get().getSeason().getTmdbSeasonId(), userWatchedTvEpisodeAddDto.getUserMail() , userWatchedTvEpisodeAddDto.getTvTmdbId());
        }

        // @TODO methode pour ajouter le temps de visionnage de l'épisode

        return true;
    }

//    private Status updateSerieStatus (Long tvTmdbId, Long userId) {
//
//        Serie serie = this.tvRepository.findByTmdbId(tvTmdbId).orElseThrow(() -> new IllegalStateException(basicErrorMessage));
//        serie.forE
//
//    }

    public Long getNbEpisodesWatchedForSeason(Long tvSeasonTmdbId,String username){
        Optional<User> optionalUser = userRepository.findUserByEmail(username);
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        Long userId = user.getId();

        List<SeasonHasEpisode> allEpisodes = this.seasonHasEpisodeRepository.findBySeasonImdbId(tvSeasonTmdbId);
        AtomicInteger nbEpisodesSeen = new AtomicInteger();
        allEpisodes.forEach(episode -> {
            Optional<UsersWatchedEpisode> optionalUserWatchedEpisode = this.usersWatchedEpisodeRepository.findByEpisodeIdAndUserId(episode.getEpisode().getId(), userId);
            if(optionalUserWatchedEpisode.isPresent()) {
                nbEpisodesSeen.getAndIncrement();
            }
        });
        return nbEpisodesSeen.longValue();
    }

    public Long getNbSeasonsWatchedForSerie(Long tvImdbId,User user){

        Long userId = user.getId();

        List<SerieHasSeason> allSeasons = this.serieHasSeasonRepository.findAllRelatedSeason(tvImdbId);
        AtomicInteger nbSeasonsSeen = new AtomicInteger();
        allSeasons.forEach(season -> {
            Optional<UsersWatchedSeason> optionalUserWatchedSeason =
                    this.usersWatchedSeasonRepository.findSeasonSeenByIdAndUserId(season.getSeason().getId(), userId);
            if(optionalUserWatchedSeason.isPresent()) {
                nbSeasonsSeen.getAndIncrement();
            }
        });
        return nbSeasonsSeen.longValue();
    }
    private Status updateSeasonStatus (Long tvSeasonTmdbId, String username , Long tvTmdbId) {
        Optional<User> optionalUser = userRepository.findUserByEmail(username);
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        Long userId = user.getId();

        List<SeasonHasEpisode> allEpisodes = this.seasonHasEpisodeRepository.findBySeasonImdbId(tvSeasonTmdbId);
        long nbEpisodesSeen = getNbEpisodesWatchedForSeason(tvSeasonTmdbId, username);

        Status returnedStatus = Status.NOTSEEN;
        // Si la relation n'existe pas, on la crée
        Optional<UsersWatchedSeason> optionalUserWatchedSeason = this.usersWatchedSeasonRepository.findByTmdbIdAndUserId(tvSeasonTmdbId, userId);
        if(!optionalUserWatchedSeason.isPresent()) {
            Season season = this.seasonRepository.findByTmdbSeasonId(tvSeasonTmdbId).get();
            user.getWatchedSeasons().add(season);
            this.userRepository.save(user);
        }

        // update la relation si elle existe au dessus sinon la récup puis l'update selon condition
        if(nbEpisodesSeen == allEpisodes.size()) {
            if(optionalUserWatchedSeason.isPresent()) {
                optionalUserWatchedSeason.get().setStatus(Status.SEEN);
                returnedStatus = Status.SEEN;
                this.usersWatchedSeasonRepository.save(optionalUserWatchedSeason.get());
                updateSerieStatus(user,tvTmdbId);

            }else{
                Optional<UsersWatchedSeason> userWatchedSeason = this.usersWatchedSeasonRepository.findByTmdbIdAndUserId(tvSeasonTmdbId, userId);
                if (userWatchedSeason.isPresent()) {
                    userWatchedSeason.get().setStatus(Status.SEEN);
                    returnedStatus = Status.SEEN;
                    this.usersWatchedSeasonRepository.save(userWatchedSeason.get());
                    updateSerieStatus(user, tvTmdbId);
                }
            }
        }else if(nbEpisodesSeen > 0) {
            if(optionalUserWatchedSeason.isPresent()) {
                optionalUserWatchedSeason.get().setStatus(Status.WATCHING);
                returnedStatus = Status.WATCHING;
                this.usersWatchedSeasonRepository.save(optionalUserWatchedSeason.get());
                updateSerieStatus(user, tvTmdbId);

            }else{
                Optional<UsersWatchedSeason> userWatchedSeason = this.usersWatchedSeasonRepository.findByTmdbIdAndUserId(tvSeasonTmdbId, userId);
                if (userWatchedSeason.isPresent()) {
                    userWatchedSeason.get().setStatus(Status.WATCHING);
                    returnedStatus = Status.WATCHING;
                    this.usersWatchedSeasonRepository.save(userWatchedSeason.get());
                    updateSerieStatus(user, tvTmdbId);
                }
            }
        }
        return returnedStatus;
    }

    private void updateSerieStatus(User user, Long tvTmdbId) {
        Optional<UsersWatchedSeries> optionalUserWatchedSeries = this.usersWatchedSeriesRepository.findByImdbIdAndUserMail(tvTmdbId, user.getUsername());
//        List<UsersWatchedSeason> watchedSeasons = this.usersWatchedSeasonRepository.findBySerieTmdbId(tvTmdbId);

        List<SerieHasSeason> allSeasons = this.serieHasSeasonRepository.findAllRelatedSeason(tvTmdbId);
        long nbSeasonsSeen = getNbSeasonsWatchedForSerie(tvTmdbId, user);

        if(!optionalUserWatchedSeries.isPresent()) {
            Serie serie = this.serieRepository.findSerieByTmdbId(tvTmdbId).get();
            user.getWatchedSeries().add(serie);
            this.userRepository.save(user);
        }

        if(nbSeasonsSeen > 0) {
            if(optionalUserWatchedSeries.isPresent()) {
                optionalUserWatchedSeries.get().setStatus(Status.WATCHING);
                this.usersWatchedSeriesRepository.save(optionalUserWatchedSeries.get());
            }else{
                Optional<UsersWatchedSeries> userWatchedSeries = this.usersWatchedSeriesRepository.findByImdbIdAndUserMail(tvTmdbId, user.getUsername());
                if (userWatchedSeries.isPresent()) {
                    userWatchedSeries.get().setStatus(Status.WATCHING);
                    this.usersWatchedSeriesRepository.save(userWatchedSeries.get());
                }
            }
        }

        if(nbSeasonsSeen == allSeasons.size()) {
            if(optionalUserWatchedSeries.isPresent()) {
                optionalUserWatchedSeries.get().setStatus(Status.SEEN);
                this.usersWatchedSeriesRepository.save(optionalUserWatchedSeries.get());
            }else{
                Optional<UsersWatchedSeries> userWatchedSeries = this.usersWatchedSeriesRepository.findByImdbIdAndUserMail(tvTmdbId, user.getUsername());
                if (userWatchedSeries.isPresent()) {
                    userWatchedSeries.get().setStatus(Status.SEEN);
                    this.usersWatchedSeriesRepository.save(userWatchedSeries.get());
                }
            }
        }
    }

    public Episode getLastSeenEpisode(UserWatchedSerieAddDto userWatchedSerieAddDto){
        // récup tous les épisodes liés à userWatchedSerieAddDto.getTmdbId()

        Optional<Serie> serie = this.serieRepository.findSerieByTmdbId(
                userWatchedSerieAddDto.getTmdbId()
        );
        if(serie.isEmpty()) {
            return new Episode(
                    0L, "Serie not downloaded yet" , 1L, 1L
            );

        }
        ArrayList<Episode> EpisodesSeen = new ArrayList<>();
        serie.get().getHasSeason().forEach(season -> {
            season.getHasEpisode().forEach(episode -> {
                Optional<Episode> ep = this.usersWatchedEpisodeRepository.isEpisodeWatchedByUser(
                        userWatchedSerieAddDto.getUserMail(),
                        episode.getId()
                );
                ep.ifPresent(EpisodesSeen::add);
            });
        });
        if(EpisodesSeen.isEmpty()) {
            return new Episode(
                    0L, "no realtion with this user yet" , 1L, 1L
            );
        }

        AtomicReference<Episode> latestEpisode = new AtomicReference<>(EpisodesSeen.get(0));

        EpisodesSeen.forEach(episode -> {
            // return episode with the highest season_number and episode_number
            if(episode.getSeason_number() >= latestEpisode.get().getSeason_number()
                    && episode.getEpisode_number() >= latestEpisode.get().getEpisode_number()) {
                latestEpisode.set(episode);
            }
        });

        //vérif que latestEpisode est contenu dans serie
        serie.get().getHasSeason().forEach(season -> {
            if(season.getSeason_number() == latestEpisode.get().getSeason_number()) {
                Long upperEpisodeNumber = latestEpisode.get().getEpisode_number() + 1L;
                season.getHasEpisode().forEach(episode -> {
                    if(episode.getEpisode_number() == upperEpisodeNumber) {
                        latestEpisode.set(episode);
                    }
                });
            }

        });
        return latestEpisode.get();
    }

    private boolean checkAllSeasonSeen (Long tvTmdbId, Long userId) {
        // select all seasons realted to tvTmdbId in serie_has_season
        List<SerieHasSeason> allSeasons =  this.serieHasSeasonRepository.findAllRelatedSeason(tvTmdbId);
        boolean allSeasonsSeen = true;
        for (int i = 0; i < allSeasons.size(); i++) {
            if(this.usersWatchedSeasonRepository.findSeasonSeenByIdAndUserId( allSeasons.get(i).getSeason().getId(), userId).isEmpty()){
                allSeasonsSeen = false;
            }
        }
        return allSeasonsSeen;
    }

    public Status addSeasonInWatchlist(UserWatchedTvSeasonAddDto userWatchedTvSeasonAddDto ) throws URISyntaxException, IOException, InterruptedException {
        //create serie or get it
        Serie serie = this.tvService.getSerieOrCreateIfNotExist(userWatchedTvSeasonAddDto.getTvTmdbId());
        Status returnedStatus = Status.NOTSEEN;
        //get user
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedTvSeasonAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));

        //seek relation user_season
        Optional<UsersWatchedSeason> relationUserSeason =  usersWatchedSeasonRepository.findByTmdbIdAndUserId(
                userWatchedTvSeasonAddDto.getTvSeasonid(),
                user.getId()
        );

        // create season if required
        if(relationUserSeason.isEmpty()) {
            Optional<Season> seasonToAdd = serie
                    .getHasSeason()
                    .stream()
                    .filter(season -> season.getTmdbSeasonId().equals(userWatchedTvSeasonAddDto.getTvSeasonid()))
                    .findFirst();

            // add season to user season's count
            if(!user.getWatchedSeasons().contains(seasonToAdd.get())){
                user.getWatchedSeasons().add(seasonToAdd.get());
                userRepository.save(user);
                // @TODO ajouter le temps de la série au compteur
            }

            // seek new relation user_season and update status
            Optional<UsersWatchedSeason> relationUserSeasonWhenCreated =  usersWatchedSeasonRepository.findByTmdbIdAndUserId(
                    userWatchedTvSeasonAddDto.getTvSeasonid(),
                    user.getId()
            );

            if(relationUserSeasonWhenCreated.isPresent()) {
                returnedStatus = Status.SEEN;
                relationUserSeasonWhenCreated.get().setStatus(Status.SEEN);
                usersWatchedSeasonRepository.save(relationUserSeasonWhenCreated.get());

                updateSerieStatus(user, serie.getTmdbId());
            }
            // create relation user_episode for every ep of the season
            createOrIncreaseUserWatchedEpisodeRelation(userWatchedTvSeasonAddDto, user);
        }else{
            // update relation to SEEN + compteur de vues
            relationUserSeason.get().setStatus(Status.SEEN);
            returnedStatus = Status.SEEN;
            relationUserSeason.get().setWatchedNumber(relationUserSeason.get().getWatchedNumber()+1L);
            // @TODO ajouter le temps de la série au compteur v2
            usersWatchedSeasonRepository.save(relationUserSeason.get());

            updateSerieStatus(user, serie.getTmdbId());

            createOrIncreaseUserWatchedEpisodeRelation(userWatchedTvSeasonAddDto, user);

        }
        return returnedStatus;
    }

    @Async
    public void createOrIncreaseUserWatchedEpisodeRelation(UserWatchedTvSeasonAddDto userWatchedTvSeasonAddDto, User user) {

        seasonHasEpisodeRepository.findBySeasonImdbId(userWatchedTvSeasonAddDto.getTvSeasonid())
                .forEach(seasonHasEpisode -> {
                    Episode episode = seasonHasEpisode.getEpisode();
                    if(!user.getWatchedEpisodes().contains(episode)){
                        user.getWatchedEpisodes().add(episode);
                        userRepository.save(user);
                    }else{
                        // increase watch_number
                        Optional<UsersWatchedEpisode> usersWatchedEpisode = usersWatchedEpisodeRepository.findByEpisodeImdbIdAndUserId(episode.getImbd_id(),user.getId());
                        usersWatchedEpisode.get().setWatchedNumber(usersWatchedEpisode.get().getWatchedNumber()+1L);
                        usersWatchedEpisodeRepository.save(usersWatchedEpisode.get());
                    }
                });
    }

    public StatusDto isTvInWatchlist(UserWatchedSerieAddDto userWatchedSerieAddDto) {

        Optional<UsersWatchedSeries> serieStatus = usersWatchedSeriesRepository.findByImdbIdAndUserMail(
                userWatchedSerieAddDto.getTmdbId(),
                userWatchedSerieAddDto.getUserMail()
        );

        Status status = serieStatus.isPresent() ? serieStatus.get().getStatus() : Status.NOTSEEN;

        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(status.getValue());

        return  statusDto;
    }

    public void removeMovieInWatchlist(UserWatchedMovieAddDto userWatchedMovieAddDto) throws URISyntaxException, IOException, InterruptedException {
        Movie movie = movieService.getMovieOrCreateIfNotExist(userWatchedMovieAddDto.getTmdbId(),
                                                              userWatchedMovieAddDto.getMovieName());
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        Movie movieToRemove = movieRepository.findByTmdbId(userWatchedMovieAddDto.getTmdbId()).orElseThrow(() -> new IllegalStateException(basicErrorMessage));
        Long movieId = movieToRemove.getId();
        Long userId = user.getId();
        UsersWatchedMovie completeUserWatched =  usersWatchedMovieRepository.findByMovieAndUserId(movieId,userId ).orElse(null);
        multiplicatorTime = completeUserWatched!=null ? completeUserWatched.getWatchedNumber().intValue(): 1;
        this.decreaseWatchedNumber(userWatchedMovieAddDto);
        this.decreaseTotalMovieWatchedTime(userWatchedMovieAddDto);
        user
        .getWatchedMovies()
        .remove(movie);
        userRepository.save(user);
    }

    public void increaseWatchedNumber(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.isPresent()? optionalUser.get() : null;
        if(user == null){
            throw new IllegalStateException(UserNotFound);
        }
         user.setTotalMovieWatchedNumber(user.getTotalMovieWatchedNumber() + 1);
         userRepository.save(user);
    }

    public void increaseWatchedNumberSeries(UserWatchedSerieAddDto userWatchedSerieAddDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedSerieAddDto.getUserMail());
        User user = optionalUser.isPresent()? optionalUser.get() : null;
        if(user == null){
            throw new IllegalStateException(UserNotFound);
        }
        user.setTotalSeriesWatchedNumber(user.getTotalSeriesWatchedNumber() + 1L);
        userRepository.save(user);
    }

    public void increaseWatchedNumberEpisode(UserWatchedTvEpisodeAddDto userWatchedTvEpisodeAddDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedTvEpisodeAddDto.getUserMail());
        User user = optionalUser.isPresent()? optionalUser.get() : null;
        if(user == null){
            throw new IllegalStateException(UserNotFound);
        }
        user.setTotalEpisodesWatchedNumber(user.getTotalEpisodesWatchedNumber() + 1L);
        userRepository.save(user);
    }
    public void decreaseWatchedNumber(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        User user = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail()).orElseThrow(()->new IllegalStateException(UserNotFound));
        Long movieId = movieRepository.findByTmdbId(userWatchedMovieAddDto.getTmdbId()).orElseThrow(()->new IllegalStateException(basicErrorMessage)).getId();
        Long userId = user.getId();
        UsersWatchedMovie completeUserWatched =  usersWatchedMovieRepository.findByMovieAndUserId(movieId,userId )
                .orElseThrow(() -> {
                    throw new IllegalStateException(UserNotFound);
                });
        user.setTotalMovieWatchedNumber(user.getTotalMovieWatchedNumber() - completeUserWatched.getWatchedNumber());
        userRepository.save(user);
    }
    public void increaseTotalMovieWatchedTime(UserWatchedMovieAddDto userWatchedMovieAddDto) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String urlToCall =  "https://api.themoviedb.org/3/movie/" +  userWatchedMovieAddDto.getTmdbId() + "?api_key=" + apiKey;
        HttpRequest getKeywordsFromCurrentFavMovieRequest = HttpRequest.newBuilder()
                .uri(new URI(urlToCall))
                .GET()
                .build();
        HttpResponse response = client.send(getKeywordsFromCurrentFavMovieRequest, HttpResponse.BodyHandlers.ofString());
        JSONObject documentObj = new JSONObject(response.body().toString());
        Gson gson = new Gson();
        SearchSingleMovieApiDto result_search = gson.fromJson(String.valueOf(documentObj), SearchSingleMovieApiDto.class);
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.isPresent()? optionalUser.get() : null;
        if(user == null){
            throw new IllegalStateException(UserNotFound);
        }
        Long newWatchedTotalTime = user.getTotalMovieWatchedTime().getSeconds()+Duration.ofSeconds(result_search.getRuntime()*60L).getSeconds();
        user.setTotalMovieWatchedTime(Duration.ofSeconds(newWatchedTotalTime));

        userRepository.save(user);
    }
    public void decreaseTotalMovieWatchedTime(UserWatchedMovieAddDto userWatchedMovieAddDto) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String urlToCall =  "https://api.themoviedb.org/3/movie/" +  userWatchedMovieAddDto.getTmdbId() + "?api_key=" + apiKey;
        HttpRequest getKeywordsFromCurrentFavMovieRequest = HttpRequest.newBuilder()
                .uri(new URI(urlToCall))
                .GET()
                .build();
        HttpResponse response = client.send(getKeywordsFromCurrentFavMovieRequest, HttpResponse.BodyHandlers.ofString());
        JSONObject documentObj = new JSONObject(response.body().toString());
        Gson gson = new Gson();
        SearchSingleMovieApiDto result_search = gson.fromJson(String.valueOf(documentObj), SearchSingleMovieApiDto.class);
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.isPresent()? optionalUser.get() : null;
        if(user == null){
            throw new IllegalStateException(UserNotFound);
        }
        Long newWatchedTotalTime = user.getTotalMovieWatchedTime().getSeconds()-Duration.ofSeconds(result_search.getRuntime()*60L).getSeconds()*multiplicatorTime;
        user.setTotalMovieWatchedTime(Duration.ofSeconds(newWatchedTotalTime));

        userRepository.save(user);
    }
    public UploadPictureDtoResponse uploadProfilePic(String email, @RequestParam("file") MultipartFile file) throws IOException {
        User user = userRepository.findUserByEmail(email)
                                  .orElseThrow(() -> new IllegalStateException(
                                          ("user with id " + email + "does not exists")));
        String fileName = user.getId() + "_profile_pic." + file.getOriginalFilename().split("\\.")[1];
        AWSCredentials credentials = new BasicAWSCredentials(
                this.awsAccessKey,
                this.awsSecretKey
        );


        Path currentRelativePath = Paths.get("");
        String basePath = currentRelativePath.toAbsolutePath().toString();

        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();
        s3client.deleteObject(this.bucketName,fileName);
        file.transferTo( new File(basePath + tempPathName + fileName));
        File originalFile = new File(basePath + tempPathName +fileName);
        File fileToUpload = new File( basePath + "/src/main/profile_pic_temp/"+fileName);
        BufferedImage originalImage = ImageIO.read(originalFile);
        File output = fileToUpload;
        originalImage = this.removeAlphaChannel(originalImage);
         long size = originalFile.length();
        float quality = 0.0f;
        if(size<3145728) {
            quality = 1.0f;
        }
        if(size> 3145728 && size < 5242880) {
            quality = 0.5f;
        }
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(output)) {
            writer.setOutput(outputStream);
            writer.write(null, new IIOImage(originalImage, null, null), param);
        }
        s3client.putObject(
                this.bucketName,
                fileName,
                fileToUpload
        );
        String key = s3client.getObject(this.bucketName,fileName).getKey();
        String url = s3client.getUrl(this.bucketName, key).toString();
        user.setProfilePicture(url+"?"+System.currentTimeMillis());
        userRepository.save(user);
        fileToUpload.delete();
        originalFile.delete();
        UploadPictureDtoResponse uploadPictureDtoResponse = new UploadPictureDtoResponse();
        uploadPictureDtoResponse.setNewPictureUrl(url);
        return uploadPictureDtoResponse;
    }

    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("email-smtp.us-east-2.amazonaws.com");
        mailSender.setPort(587);

        mailSender.setUsername(awsSesAccessKey);
        mailSender.setPassword(awsSesSecretKey);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
    public int sendEmailReset(ResetPasswordMailingDto ResetPasswordMailingDto) throws MessagingException {
        Optional<User> optionalUser = userRepository.findUserByEmail(ResetPasswordMailingDto.getUsername());
        User user = optionalUser.isPresent()? optionalUser.get() : null;
        if(user == null){
            throw new IllegalStateException(UserNotFound);
        }
        if(user.getDateLastMailingResetPassword()!=null){
            boolean delayCheck = user.getDateLastMailingResetPassword().plusMinutes(30).isBefore(LocalDateTime.now());
            if(!delayCheck){
                return 403;
            }
        }
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
        String token = JWT.create()
                .withIssuer("auth0")
                .sign(algorithm);
        JavaMailSender MailerService = this.getJavaMailSender();
        MimeMessage mimeMessage = MailerService.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        String htmlMsg = "<h3>Hello " + ResetPasswordMailingDto.getUsername() + " . </h3> \n You have asked to reset your password. Please click on the link below to reset your password: \n <a href=\""+resetPasswordUrl + token + "\">Reset password</a>";
//mimeMessage.setContent(htmlMsg, "text/html"); /** Use this or below line **/
        helper.setText(htmlMsg, true); // Use this or above line.
        helper.setTo(ResetPasswordMailingDto.getUsername());
        helper.setSubject("Reset password");
        helper.setFrom(mailOrigin);
        MailerService.send(helper.getMimeMessage());
        user.setDateLastMailingResetPassword(LocalDateTime.now());
        user.setTokenResetPassword(token);
        userRepository.saveAndFlush(user);
        return 200;
    }

    public boolean checkToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build(); //Reusable verifier instance
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception){
            return false;
        }
    }
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    public int changeUserPassword(ResetPasswordUseDto resetPasswordUseDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(resetPasswordUseDto.getEmail());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        boolean checkToken = this.checkToken(resetPasswordUseDto.getToken());
        PasswordEncoder passwordEncoder = this.encoder();
        if(checkToken && user.getTokenResetPassword().equals(resetPasswordUseDto.getToken())){
            user.setPassword(passwordEncoder.encode(resetPasswordUseDto.getPassword()));
            user.setTokenResetPassword(null);
            userRepository.saveAndFlush(user);
            return 200;
        }
        return 401;
    }

    public ProfileLazyUserDtoHeader getProfileHeaderData(String email) throws URISyntaxException, IOException, InterruptedException {
        String inCachePrefix = "profileHeaderData";
        String inCacheKey = inCachePrefix + email;
        String test = (String) hazelcastConfig.hazelcastInstance().getMap(inCachePrefix).get(inCacheKey);
          if(test!=null){
              return new ObjectMapper().readValue(test, ProfileLazyUserDtoHeader.class);
          }

        String checkInCache = this.redisService.getRedisCacheDataBDD(inCacheKey);
        if(checkInCache!=null){
            return new ObjectMapper().readValue(checkInCache, ProfileLazyUserDtoHeader.class);
        }
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        ProfileLazyUserDtoHeader profileLazyUserDtoHeader = new ProfileLazyUserDtoHeader();
        profileLazyUserDtoHeader.setNumberOfWatchedSeries(user.getTotalSeriesWatchedNumber());
        profileLazyUserDtoHeader.setNumberOfWatchedMovies(user.getTotalMovieWatchedNumber());
        String totalDurationMoviesMonthDayHour = durationConvertor(user.getTotalMovieWatchedTime());
        profileLazyUserDtoHeader.setTotalTimeWatchedMovies(totalDurationMoviesMonthDayHour);
        String totalDurationSeriesMonthDayHour = durationConvertor(user.getTotalSeriesWatchedTime());
        profileLazyUserDtoHeader.setTotalTimeWatchedSeries(totalDurationSeriesMonthDayHour);
        hazelcastConfig.hazelcastInstance().getMap(inCachePrefix).set(inCacheKey,new ObjectMapper().writeValueAsString(profileLazyUserDtoHeader), 1, TimeUnit.MINUTES);
        this.redisService.setRedisCacheDataBDD(inCacheKey, new ObjectMapper().writeValueAsString(profileLazyUserDtoHeader),60);
        return profileLazyUserDtoHeader;
    }

    public String durationConvertor(Duration duration){
        duration = Duration.ofDays(duration.toDaysPart()).plusHours(duration.toHoursPart());

        Period period = Period.between(LocalDate.ofEpochDay(0), LocalDate.ofEpochDay(duration.toDays()));
        int months = period.getMonths();
        int days = period.getDays();
        int hours = (int) (duration.toHours() - (days + months * 30) * 24);
        return months + "/" + days + "/" + hours;
    }

    public ProfileLazyUserDtoLastWatchedMovies getProfileLastWatchedMoviesData(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
         Optional<long[]> lastWatchedMoviesIds = usersWatchedMovieRepository.findWatchedMoviesByUserId(user.getId());

        ProfileLazyUserDtoLastWatchedMovies profileLazyUserDtoLastWatchedMovies = new ProfileLazyUserDtoLastWatchedMovies();
        long[] favoriteMoviesIds = new long[user.getFavoriteMovies().size()>=10?10:user.getFavoriteMovies().size()];
        int i = 0;
        for(Movie movie : user.getFavoriteMovies()){
            if(i==10){
                break;
            }
            favoriteMoviesIds[i] = movie.getTmdbId();
            i++;
        }
        long[] watchlistMoviesIds = new long[user.getWatchlistMovies().size()>=10?10:user.getWatchlistMovies().size()];
        i = 0;
        for(Movie movie : user.getWatchlistMovies()){
            if(i==10){
                break;
            }
            watchlistMoviesIds[i] = movie.getTmdbId();
            i++;
        }
        profileLazyUserDtoLastWatchedMovies.setLastWatchedMovies(Arrays.stream(lastWatchedMoviesIds.isPresent()?lastWatchedMoviesIds.get():null).limit(10).toArray());
        profileLazyUserDtoLastWatchedMovies.setFavoritesMovies(favoriteMoviesIds);
        profileLazyUserDtoLastWatchedMovies.setWatchlistMovies(watchlistMoviesIds);
        profileLazyUserDtoLastWatchedMovies.setTotalFavoritesMovies(user.getFavoriteMovies().size());
        profileLazyUserDtoLastWatchedMovies.setTotalWatchedMovies(lastWatchedMoviesIds.isPresent()?lastWatchedMoviesIds.get().length:0);
        profileLazyUserDtoLastWatchedMovies.setTotalWatchlistMovies(user.getWatchlistMovies().size());
        return profileLazyUserDtoLastWatchedMovies;
    }



    public ProfileLazyUserDtoSocialInfos getProfileSocialInfos(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        ProfileLazyUserDtoSocialInfos profileLazyUserDtoSocialInfos = new ProfileLazyUserDtoSocialInfos();
        profileLazyUserDtoSocialInfos.setFollowersCounter(user.getFollowersCounter());
        profileLazyUserDtoSocialInfos.setFollowingsCounter(user.getFollowingsCounter());
        profileLazyUserDtoSocialInfos.setCommentsCounter(user.getCommentsCounter());
        return profileLazyUserDtoSocialInfos;


    }

    public ProfileLazyUserDtoAvatar getProfileAvatar(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        ProfileLazyUserDtoAvatar profileLazyUserDtoAvatar = new ProfileLazyUserDtoAvatar();
        profileLazyUserDtoAvatar.setProfilePicture(user.getProfilePicture()==null?"":user.getProfilePicture());
        profileLazyUserDtoAvatar.setBackgroundPicture(user.getBackgroundPicture()==null?"":user.getBackgroundPicture());
        profileLazyUserDtoAvatar.setFullName(user.getFullName());
        return profileLazyUserDtoAvatar;
    }


    public UploadBackgroundDtoResponse uploadBackgroundPic(String email, @RequestParam("file") MultipartFile file) throws IOException {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        ("user with id " + email + "does not exists")));
        if(file.isEmpty()){
            throw new IllegalStateException("File is empty");
        }
        String fileName = user.getId() + "_background_pic." + file.getOriginalFilename().split("\\.")[1];
        AWSCredentials credentials = new BasicAWSCredentials(
                this.awsAccessKey,
                this.awsSecretKey
        );

        Path currentRelativePath = Paths.get("");
        String basePath = currentRelativePath.toAbsolutePath().toString();

        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();
        s3client.deleteObject(this.bucketName,fileName);
        file.transferTo( new File(basePath + tempPathName +fileName));
        File originalFile = new File(basePath + tempPathName +fileName);

        File fileToUpload = new File( basePath + "/src/main/profile_pic_temp/"+fileName);
        BufferedImage originalImage = ImageIO.read(originalFile);
        File output = fileToUpload;
        originalImage = this.removeAlphaChannel(originalImage);
        long size = originalFile.length();
        float quality = 0.0f;
        if(size<3145728) {
            quality = 1.0f;
        }
        if(size> 3145728 && size < 5242880) {
            quality = 0.5f;
        }
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(output)) {
            writer.setOutput(outputStream);
            writer.write(null, new IIOImage(originalImage, null, null), param);
        }
        s3client.putObject(
                this.bucketName,
                fileName,
                fileToUpload
        );
        String key = s3client.getObject(this.bucketName,fileName).getKey();
        String url = s3client.getUrl(this.bucketName, key).toString();
        user.setBackgroundPicture(url+"?"+System.currentTimeMillis());
        userRepository.save(user);
        if(fileToUpload.delete()){
            LOGGER.print("File deleted successfully");
        }
        if(originalFile.delete()){
            LOGGER.print("File deleted successfully");
        }
        UploadBackgroundDtoResponse uploadBackgroundDtoResponse = new UploadBackgroundDtoResponse();
        uploadBackgroundDtoResponse.setNewBackgroundUrl(url);
        return uploadBackgroundDtoResponse;
    }

    public boolean toggleMovieInFavoritelist(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        Movie movie = movieService.getMovieOrCreateIfNotExist(userWatchedMovieAddDto.getTmdbId(),
                userWatchedMovieAddDto.getMovieName());

        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.isPresent()? optionalUser.get() : null;
        if(user == null){
            throw new IllegalStateException(UserNotFound);
        }
        if(!user.getFavoriteMovies().contains(movie)){
            user
            .getFavoriteMovies()
            .add(movie);
            userRepository.save(user);
            return true;
        }
        if(user.getFavoriteMovies().contains(movie)){
            user
            .getFavoriteMovies()
            .remove(movie);
            userRepository.save(user);
            return false;
        }
        return false;
    }
    public boolean toggleMovieInMovieToWatchlist(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        Movie movie = movieService.getMovieOrCreateIfNotExist(userWatchedMovieAddDto.getTmdbId(),
                userWatchedMovieAddDto.getMovieName());

        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.isPresent()? optionalUser.get() : null;
        if(user == null){
            throw new IllegalStateException(UserNotFound);
        }
        if(!user.getWatchlistMovies().contains(movie)){
            user
                    .getWatchlistMovies()
                    .add(movie);
            userRepository.save(user);
            return true;
        }
        if(user.getWatchlistMovies().contains(movie)){
            user
            .getWatchlistMovies()
            .remove(movie);
            userRepository.save(user);
            return false;
        }
        return false;
    }

    public boolean isMovieInMovieToWatchlist(UserWatchedMovieAddDto userWatchedMovieDto) {
        Optional<UserSimpleDto> user = userRepository.isMovieInMovieToWatch(
                userWatchedMovieDto.getUserMail(), userWatchedMovieDto.getTmdbId());
        return user.isPresent();
    }

    public boolean isMovieInFavoritelist(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        Optional<UserSimpleDto> user = userRepository.isMovieInFavorite(
                userWatchedMovieAddDto.getUserMail(), userWatchedMovieAddDto.getTmdbId());
        return user.isPresent();
    }

    public fetchRangeListDto lastWatchedMoviesRange(fetchRangeDto fetchRangeDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(fetchRangeDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        Optional<long[]> lastWatchedMoviesIds = usersWatchedMovieRepository.findWatchedMoviesByUserId(user.getId());
        long[] stream = lastWatchedMoviesIds.isPresent() ? lastWatchedMoviesIds.get() : new long[0];
        long[] listToUse = Arrays.stream(stream)
                .skip(fetchRangeDto.getCurrentLength())
                .limit(fetchRangeDto.getCurrentLength()+10L)
        .toArray();
        fetchRangeListDto fetchRangeListDto = new fetchRangeListDto();
        fetchRangeListDto.setTmdbIdList(listToUse);
        return fetchRangeListDto;
    }
    public fetchRangeListDto favoritesMoviesRange(fetchRangeDto fetchRangeDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(fetchRangeDto.getUserMail());
        User user = optionalUser.isPresent()? optionalUser.get() : null;
        if(user == null){
            throw new IllegalStateException(UserNotFound);
        }
        Movie[] listRange = Arrays.stream(user.getFavoriteMovies().toArray())
                .skip(fetchRangeDto.getCurrentLength())
                .limit(fetchRangeDto.getCurrentLength()+10L)
                .toArray(Movie[]::new);
        return getFetchRangeListDto(listRange);
    }
    public fetchRangeListDto watchlistMoviesRange(fetchRangeDto fetchRangeDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(fetchRangeDto.getUserMail());
        User user = optionalUser.isPresent()? optionalUser.get() : null;
        if(user == null){
            throw new IllegalStateException(UserNotFound);
        }
        Movie[] listRange = Arrays.stream(user.getWatchlistMovies().toArray())
                .skip(fetchRangeDto.getCurrentLength())
                .limit(fetchRangeDto.getCurrentLength()+10L)
                .toArray(Movie[]::new);
        return getFetchRangeListDto(listRange);
    }

    private fetchRangeListDto getFetchRangeListDto(Movie[] listRange) {
        long [] listToUse = new long[listRange.length];
        int i = 0;
        for (Movie movie : listRange){
            listToUse[i]=movie.getTmdbId();
            i++;
        }
        fetchRangeListDto fetchRangeListDto = new fetchRangeListDto();
        fetchRangeListDto.setTmdbIdList(listToUse);
        return fetchRangeListDto;
    }

    public BufferedImage removeAlphaChannel(BufferedImage img) {
        if (!img.getColorModel().hasAlpha()) {
            return img;
        }

        BufferedImage target = createImage(img.getWidth(), img.getHeight(), false);
        Graphics2D g = target.createGraphics();
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return target;
    }
    public BufferedImage createImage(int width, int height, boolean hasAlpha) {
        return new BufferedImage(width, height, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
    }

    public boolean isEpisodeInWatchlist(UserWatchedEpisodeDto userWatchedEpisodeDto) {
        Optional<UsersWatchedEpisode> seen = userRepository.isEpisodeWatched(
                userWatchedEpisodeDto.getUserMail(),
                userWatchedEpisodeDto.getEpisodeTmdbId()
        );
        if(seen.isPresent()){
            return true;
        }else{
            return false;
        }

    }

    public Status isSeasonInWatchlist(UserWatchedTvSeasonAddDto userWatchedTvSeasonAddDto) {
        Status status = Status.NOTSEEN;
        if(userWatchedTvSeasonAddDto.getTvSeasonid() == null){
            return  status;
        }
        Optional<UsersWatchedSeason> seasonStatus = userRepository.getSeasonStatus(
                userWatchedTvSeasonAddDto.getUserMail(),
                userWatchedTvSeasonAddDto.getTvSeasonid()

        );
        if(seasonStatus.isPresent()){
            status = seasonStatus.get().getStatus();
        }
        return  status;
    }

    public ArrayList<Long> fetchTvWatching(UserMailDto userMailDto) {
        Optional<UsersWatchedSeries[]> watchingSeriesRelation = usersWatchedSeriesRepository.getWatchingSeries(userMailDto.getUserMail());
        return getTvIdsFromList(watchingSeriesRelation);
    }

    public ArrayList<Long> fetchTvWatched(UserMailDto userMailDto) {
        Optional<UsersWatchedSeries[]> watchingSeriesRelation = usersWatchedSeriesRepository.getWatchedSeries(userMailDto.getUserMail());
        return getTvIdsFromList(watchingSeriesRelation);
    }

    private ArrayList<Long> getTvIdsFromList(Optional<UsersWatchedSeries[]> watchingSeriesRelation) {
        ArrayList<Long> seriesWatching = new ArrayList<>();
        if(watchingSeriesRelation.isPresent()){

            for(UsersWatchedSeries usersWatchedSeries : watchingSeriesRelation.get()){
                seriesWatching.add(usersWatchedSeries.getSerie().getTmdbId());
            }
            return seriesWatching;
        }else{
            return null;
        }
    }
}