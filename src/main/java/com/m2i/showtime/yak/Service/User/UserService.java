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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.gson.Gson;
import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.*;
import com.m2i.showtime.yak.Repository.ActorRepository;
import com.m2i.showtime.yak.Repository.CommentRepository;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import com.m2i.showtime.yak.Repository.UsersWatchedMovieRepository;
import com.m2i.showtime.yak.Service.*;
import com.m2i.showtime.yak.Repository.*;
import com.m2i.showtime.yak.Enum.Status;
import com.m2i.showtime.yak.common.notification.NotificationStatus;
import com.m2i.showtime.yak.common.trophy.TrophyActionName;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
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
import javax.persistence.EntityManager;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@EnableAsync
public class UserService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    private final SerieRepository serieRepository;

    private final TvRepository tvRepository;
    private final MovieService movieService;
    private final CommentRepository commentRepository;
    private final ActorRepository actorRepository;
    private final GenreRepository genreRepository;
    private final TvService tvService;
    private final UsersWatchedMovieRepository usersWatchedMovieRepository;
    private final KafkaMessageGeneratorService kafkaMessageGeneratorService;
    private final TrophyService trophyService;

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
    @Value("${external.service.imdb.apiKey}")
    private String apiKey;
    @Value("${spring.mail.resetPasswordUrl}")
    private String resetPasswordUrl;
    @Value("${spring.mail.mailOrigin}")
    private String mailOrigin;
    @Value("${spring.jwt.secretKey}")
    private String JWT_SECRET;
    private int multiplicatorTime = 1;
    private RedisService redisService;
    private final String UserNotFound = "User not found";
    @Value("${spring.tempPathName}")
    private String tempPathName;
    private final String basicErrorMessage = "Something went wrong";
    private LoggerService LOGGER = new LoggerService();
    private final UsersWatchedSeriesRepository usersWatchedSeriesRepository;
    private final UsersWatchedEpisodeRepository usersWatchedEpisodeRepository;
    private final UsersWatchedSeasonRepository usersWatchedSeasonRepository;
    private final SeasonRepository seasonRepository;
    private final SeasonHasEpisodeRepository seasonHasEpisodeRepository;
    private final SerieHasSeasonRepository serieHasSeasonRepository;


    private final UserAuthService userAuthService;
    @Value("${spring.profiles.active}")
    private String ENV;
    private final TrophyRepository trophyRepository;
    private final EpisodeRepository episodeRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       MovieRepository movieRepository,
                       MovieService movieService,
                       CommentRepository commentRepository,
                       GenreRepository genreRepository,
                       UsersWatchedMovieRepository usersWatchedMovieRepository,
                       KafkaMessageGeneratorService kafkaMessageGeneratorService,
                       RedisService redisService,
                       LoggerService LOGGER,
                       UserAuthService userAuthService,
                       ActorRepository actorRepository,
                       UsersWatchedSeriesRepository usersWatchedSeriesRepository,
                       UsersWatchedEpisodeRepository usersWatchedEpisodeRepository,
                       UsersWatchedSeasonRepository usersWatchedSeasonRepository,
                       SeasonRepository seasonRepository,
                       TvRepository tvRepository,
                       SeasonHasEpisodeRepository seasonHasEpisodeRepository,
                       SerieHasSeasonRepository serieHasSeasonRepository,
                       SerieRepository serieRepository,
                       TvService tvService,
                       TrophyService trophyService,
                       TrophyRepository trophyRepository,
                       EpisodeRepository episodeRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.tvService = tvService;
        this.tvRepository = tvRepository;
        this.movieService = movieService;
        this.commentRepository = commentRepository;
        this.kafkaMessageGeneratorService = kafkaMessageGeneratorService;
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
        this.usersWatchedMovieRepository = usersWatchedMovieRepository;
        this.redisService = redisService;
        this.LOGGER = LOGGER;
        this.usersWatchedSeriesRepository = usersWatchedSeriesRepository;
        this.usersWatchedEpisodeRepository = usersWatchedEpisodeRepository;
        this.usersWatchedSeasonRepository = usersWatchedSeasonRepository;
        this.seasonRepository = seasonRepository;
        this.seasonHasEpisodeRepository = seasonHasEpisodeRepository;
        this.serieHasSeasonRepository = serieHasSeasonRepository;
        this.serieRepository = serieRepository;
        this.userAuthService = userAuthService;
        this.trophyService = trophyService;
        this.trophyRepository = trophyRepository;
        this.episodeRepository = episodeRepository;
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
        if (userOptional.isPresent()) {
            throw new IllegalStateException("email taken");
        }
        userRepository.save(user);
        return user;
    }

    public void deleteUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalStateException("User does not exists");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateUser(Long userId,
                           User modifiedUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(("user with id " + userId + "does not exists")));
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
            if (userRepository.findUserByEmail(modifiedUser.getUsername()).isPresent()) {
                throw new IllegalStateException("email taken");
            }
            user.setUsername(modifiedUser.getUsername());
        }
    }

    @Transactional
    public void editAccountInfos(Long userId,
                                 EditAccountInfosDto modifiedUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(("user with id " + userId + "does not exists")));

        new ModelMapper().map(modifiedUser, user);
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
        Optional<UsersWatchedMovie> optionalUserWatchedMovie = usersWatchedMovieRepository.findByMovieAndUserId(movieId, userId);
        if (!optionalUserWatchedMovie.isPresent()) {
            user
                    .getWatchedMovies()
                    .add(movie);
            userRepository.save(user);
            this.increaseWatchedNumber(userWatchedMovieAddDto);
            trophyService.checkAllTrophys(userWatchedMovieAddDto.getUserMail(), movie.getId(), TrophyActionName.ADD_MOVIE_IN_WATCHED_LIST);
        }
        if (optionalUserWatchedMovie.isPresent()) {
            Long currentWatchedNumber = optionalUserWatchedMovie.get().getWatchedNumber();
            optionalUserWatchedMovie.get().setWatchedNumber(currentWatchedNumber + 1L);
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
            usersWatchedSeriesRepository.save(relatedSerie.get());
            this.increaseWatchedNumberSeries(userWatchedSerieAddDto);
            trophyService.checkAllTrophys(userWatchedSerieAddDto.getUserMail(), serie.getId(), TrophyActionName.ADD_SERIE_IN_WATCHED_LIST);
        }

        //remove to watchlist bcz seen
        if (user.getWatchlistSeries().contains(serie)) {
            user.getWatchlistSeries().remove(serie);
            userRepository.save(user);
        }
        ConcurrentHashMap<Season, Boolean> watchedSeasonsMap = new ConcurrentHashMap<>();

        serie
                .getHasSeason()
                .forEach(season -> {
                    if (!watchedSeasonsMap.containsKey(season)) {

                        if (!watchedSeasonsMap.containsKey(season)) {
                            watchedSeasonsMap.put(season, true);

                            // Perform other operations related to the watched season

                            // Add the season to the user's watched seasons

                            if (!user.getWatchedSeasons().contains(season)) {
                                user.getWatchedSeasons().add(season);
                            }

                        }

                    }




                    season.getHasEpisode().forEach(episode -> {

                        if (!user.getWatchedEpisodes().contains(episode)) {
                            increaseDurationSerieDto increaseDurationSerieDto = new increaseDurationSerieDto();
                            increaseDurationSerieDto.setUsername(user.getUsername());
                            increaseDurationSerieDto.setSeasonNumber(season.getSeason_number());
                            increaseDurationSerieDto.setEpisodeNumber(episode.getEpisode_number());
                            increaseDurationSerieDto.setTvTmdbId(serie.getTmdbId());
                            try {
                                if (checkIfEpisodeOnAir(increaseDurationSerieDto)) {
                                    user.getWatchedEpisodes().add(episode);
                                    increaseWatchedDurationSeries(increaseDurationSerieDto);
                                }
                            } catch (URISyntaxException | IOException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    });
                    userRepository.save(user);
                    if(user.getWatchedEpisodes().containsAll(season.getHasEpisode())){
                        Optional<UsersWatchedSeason> relationUserSeasonWhenCreated = usersWatchedSeasonRepository.findByTmdbIdAndUserId(
                                season.getTmdbSeasonId(),
                                user.getId()
                        );
                        relationUserSeasonWhenCreated.get().setStatus(Status.SEEN);
                        usersWatchedSeasonRepository.save(relationUserSeasonWhenCreated.get());
                    }

                });

        return true;
    }

    public boolean addEpisodeInWatchlist(UserWatchedTvEpisodeAddDto userWatchedTvEpisodeAddDto) throws URISyntaxException, IOException, InterruptedException {
        Serie serie = this.tvService.getSerieOrCreateIfNotExist(userWatchedTvEpisodeAddDto.getTvTmdbId());

        // récup l'user
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedTvEpisodeAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        Long userId = user.getId();

        //remove to watchlist bcz seen
        if (user.getWatchlistSeries().contains(serie)) {
            user.getWatchlistSeries().remove(serie);
            userRepository.save(user);
        }
        increaseDurationSerieDto increaseDurationSerieDto = new increaseDurationSerieDto();
        increaseDurationSerieDto.setTvTmdbId(serie.getTmdbId());
        increaseDurationSerieDto.setUsername(userWatchedTvEpisodeAddDto.getUserMail());
        increaseDurationSerieDto.setSeasonNumber(userWatchedTvEpisodeAddDto.getSeasonNumber());
        increaseDurationSerieDto.setEpisodeNumber(userWatchedTvEpisodeAddDto.getEpisodeNumber());
        this.tvService.createEpisodeIfNotExist(increaseDurationSerieDto);
        // récup obj episode
        Episode episode = usersWatchedEpisodeRepository.findEpisodeByTmdbId(userWatchedTvEpisodeAddDto.getEpisodeId()).get();
        //Check episode is in watchedListEpisode
        Optional<UsersWatchedEpisode> optionalUserWatchedEpisode = usersWatchedEpisodeRepository.findByEpisodeIdAndUserId(episode.getId(), userId);

        // si pas présent alors add
        if (!optionalUserWatchedEpisode.isPresent()) {
            user
                    .getWatchedEpisodes()
                    .add(episode);
            userRepository.save(user);
            increaseWatchedNumberEpisode(userWatchedTvEpisodeAddDto);
        } else {
            Long currentWatchedNumber = optionalUserWatchedEpisode.get().getWatchedNumber();
            optionalUserWatchedEpisode.get().setWatchedNumber(currentWatchedNumber + 1L);
            usersWatchedEpisodeRepository.save(optionalUserWatchedEpisode.get());
            increaseWatchedNumberEpisode(userWatchedTvEpisodeAddDto);
        }
        // méthode pour rajouter la saison si tout les épisodes d'une saison sont visionnés && mettre le bon statut
        if (userWatchedTvEpisodeAddDto.getTvSeasonid() != null) {
            updateSeasonStatus(userWatchedTvEpisodeAddDto.getTvSeasonid(), userWatchedTvEpisodeAddDto.getUserMail(), userWatchedTvEpisodeAddDto.getTvTmdbId());
        } else {
            // query pour récup la saison id
            Optional<SeasonHasEpisode> seasonHasEpisode = seasonRepository.findSeasonWithEpisodeTmdbId(userWatchedTvEpisodeAddDto.getEpisodeId());
            updateSeasonStatus(seasonHasEpisode.get().getSeason().getTmdbSeasonId(), userWatchedTvEpisodeAddDto.getUserMail(), userWatchedTvEpisodeAddDto.getTvTmdbId());
        }

        increaseWatchedDurationSeries(increaseDurationSerieDto);

        return true;
    }

//    private Status updateSerieStatus (Long tvTmdbId, Long userId) {
//
//        Serie serie = this.tvRepository.findByTmdbId(tvTmdbId).orElseThrow(() -> new IllegalStateException(basicErrorMessage));
//        serie.forE
//
//    }

    public Long getNbEpisodesWatchedForSeason(Long tvSeasonTmdbId, String username) {
        Optional<User> optionalUser = userRepository.findUserByEmail(username);
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        Long userId = user.getId();

        List<SeasonHasEpisode> allEpisodes = this.seasonHasEpisodeRepository.findBySeasonImdbId(tvSeasonTmdbId);
        AtomicInteger nbEpisodesSeen = new AtomicInteger();
        allEpisodes.forEach(episode -> {
            Optional<UsersWatchedEpisode> optionalUserWatchedEpisode = this.usersWatchedEpisodeRepository.findByEpisodeIdAndUserId(episode.getEpisode().getId(), userId);
            if (optionalUserWatchedEpisode.isPresent()) {
                nbEpisodesSeen.getAndIncrement();
            }
        });
        return nbEpisodesSeen.longValue();
    }

    public Long getNbSeasonsWatchedForSerie(Long tvImdbId, User user) {

        Long userId = user.getId();

        List<SerieHasSeason> allSeasons = this.serieHasSeasonRepository.findAllRelatedSeason(tvImdbId);
        AtomicInteger nbSeasonsSeen = new AtomicInteger();
        allSeasons.forEach(season -> {
            Optional<UsersWatchedSeason> optionalUserWatchedSeason =
                    this.usersWatchedSeasonRepository.findSeasonSeenByIdAndUserId(season.getSeason().getId(), userId);
            if (optionalUserWatchedSeason.isPresent()) {
                nbSeasonsSeen.getAndIncrement();
            }
        });
        return nbSeasonsSeen.longValue();
    }

    private Status updateSeasonStatus(Long tvSeasonTmdbId, String username, Long tvTmdbId) {
        Optional<User> optionalUser = userRepository.findUserByEmail(username);
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        Long userId = user.getId();

        List<SeasonHasEpisode> allEpisodes = this.seasonHasEpisodeRepository.findBySeasonImdbId(tvSeasonTmdbId);
        long nbEpisodesSeen = getNbEpisodesWatchedForSeason(tvSeasonTmdbId, username);

        Status returnedStatus = Status.NOTSEEN;
        // Si la relation n'existe pas, on la crée
        Optional<UsersWatchedSeason> optionalUserWatchedSeason = this.usersWatchedSeasonRepository.findByTmdbIdAndUserId(tvSeasonTmdbId, userId);
        if (!optionalUserWatchedSeason.isPresent()) {
            Season season = this.seasonRepository.findByTmdbSeasonId(tvSeasonTmdbId).get();
            user.getWatchedSeasons().add(season);
            this.userRepository.save(user);
        }

        // update la relation si elle existe au dessus sinon la récup puis l'update selon condition
        if (nbEpisodesSeen == allEpisodes.size()) {
            if (optionalUserWatchedSeason.isPresent()) {
                optionalUserWatchedSeason.get().setStatus(Status.SEEN);
                returnedStatus = Status.SEEN;
                this.usersWatchedSeasonRepository.save(optionalUserWatchedSeason.get());
                updateSerieStatus(user, tvTmdbId);

            } else {
                Optional<UsersWatchedSeason> userWatchedSeason = this.usersWatchedSeasonRepository.findByTmdbIdAndUserId(tvSeasonTmdbId, userId);
                if (userWatchedSeason.isPresent()) {
                    userWatchedSeason.get().setStatus(Status.SEEN);
                    returnedStatus = Status.SEEN;
                    this.usersWatchedSeasonRepository.save(userWatchedSeason.get());
                    updateSerieStatus(user, tvTmdbId);
                }
            }
        } else if (nbEpisodesSeen > 0) {
            if (optionalUserWatchedSeason.isPresent()) {
                optionalUserWatchedSeason.get().setStatus(Status.WATCHING);
                returnedStatus = Status.WATCHING;
                this.usersWatchedSeasonRepository.save(optionalUserWatchedSeason.get());
                updateSerieStatus(user, tvTmdbId);

            } else {
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

        if (!optionalUserWatchedSeries.isPresent()) {
            Serie serie = this.serieRepository.findSerieByTmdbId(tvTmdbId).get();
            user.getWatchedSeries().add(serie);
            this.userRepository.save(user);
            trophyService.checkAllTrophys(user.getUsername(), serie.getId(), TrophyActionName.ADD_SERIE_IN_WATCHED_LIST);
        }

        if (nbSeasonsSeen > 0) {
            if (optionalUserWatchedSeries.isPresent()) {
                optionalUserWatchedSeries.get().setStatus(Status.WATCHING);
                this.usersWatchedSeriesRepository.save(optionalUserWatchedSeries.get());
            } else {
                Optional<UsersWatchedSeries> userWatchedSeries = this.usersWatchedSeriesRepository.findByImdbIdAndUserMail(tvTmdbId, user.getUsername());
                if (userWatchedSeries.isPresent()) {
                    userWatchedSeries.get().setStatus(Status.WATCHING);
                    this.usersWatchedSeriesRepository.save(userWatchedSeries.get());
                }
            }
        }

        if (nbSeasonsSeen == allSeasons.size()) {
            if (optionalUserWatchedSeries.isPresent()) {
                optionalUserWatchedSeries.get().setStatus(Status.SEEN);
                this.usersWatchedSeriesRepository.save(optionalUserWatchedSeries.get());
            } else {
                Optional<UsersWatchedSeries> userWatchedSeries = this.usersWatchedSeriesRepository.findByImdbIdAndUserMail(tvTmdbId, user.getUsername());
                if (userWatchedSeries.isPresent()) {
                    userWatchedSeries.get().setStatus(Status.SEEN);
                    this.usersWatchedSeriesRepository.save(userWatchedSeries.get());
                }
            }
        }
    }

    public ArrayList<Long> fetchLastTvSeriesWatched(UserMailDto userMailDto) {
        // get all episodes seen by user
        Optional<UsersWatchedSeries[]> usersWatchedSeries = this.usersWatchedSeriesRepository.getLastWatchedSeries(userMailDto.getUserMail());
        ArrayList<Long> seriesIds = new ArrayList<>();
        if (usersWatchedSeries.isPresent()) {
            for (UsersWatchedSeries usersWatchedSerie : usersWatchedSeries.get()) {
                seriesIds.add(usersWatchedSerie.getSerie().getTmdbId());
            }
            return seriesIds;
        } else {
            return null;
        }
    }


    public Episode getLastSeenEpisode(UserWatchedSerieAddDto userWatchedSerieAddDto) {
        // récup tous les épisodes liés à userWatchedSerieAddDto.getTmdbId()

        Optional<Serie> serie = this.serieRepository.findSerieByTmdbId(
                userWatchedSerieAddDto.getTmdbId()
        );
        if (serie.isEmpty()) {
            return new Episode(
                    0L, "Serie not downloaded yet", 1L, 1L
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
        if (EpisodesSeen.isEmpty()) {
            return new Episode(
                    0L, "no realtion with this user yet", 1L, 1L
            );
        }

        AtomicReference<Episode> latestEpisode = new AtomicReference<>(EpisodesSeen.get(0));

        EpisodesSeen.forEach(episode -> {
            // return episode with the highest season_number and episode_number
            if (episode.getSeason_number() >= latestEpisode.get().getSeason_number()
                    && episode.getEpisode_number() >= latestEpisode.get().getEpisode_number()) {
                latestEpisode.set(episode);
            }
        });

        //vérif que latestEpisode est contenu dans serie
        serie.get().getHasSeason().forEach(season -> {
            if (season.getSeason_number() == latestEpisode.get().getSeason_number()) {
                Long upperEpisodeNumber = latestEpisode.get().getEpisode_number() + 1L;
                season.getHasEpisode().forEach(episode -> {
                    if (episode.getEpisode_number() == upperEpisodeNumber) {
                        latestEpisode.set(episode);
                    }
                });
            }

        });
        return latestEpisode.get();
    }

    private boolean checkAllSeasonSeen(Long tvTmdbId, Long userId) {
        // select all seasons realted to tvTmdbId in serie_has_season
        List<SerieHasSeason> allSeasons = this.serieHasSeasonRepository.findAllRelatedSeason(tvTmdbId);
        boolean allSeasonsSeen = true;
        for (int i = 0; i < allSeasons.size(); i++) {
            if (this.usersWatchedSeasonRepository.findSeasonSeenByIdAndUserId(allSeasons.get(i).getSeason().getId(), userId).isEmpty()) {
                allSeasonsSeen = false;
            }
        }
        return allSeasonsSeen;
    }

    public Status addSeasonInWatchlist(UserWatchedTvSeasonAddDto userWatchedTvSeasonAddDto) throws URISyntaxException, IOException, InterruptedException {
        //create serie or get it
        Serie serie = this.tvService.getSerieOrCreateIfNotExist(userWatchedTvSeasonAddDto.getTvTmdbId());
        Status returnedStatus = Status.NOTSEEN;
        //get user
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedTvSeasonAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));

        //remove to watchlist bcz seen
        if (user.getWatchlistSeries().contains(serie)) {
            user.getWatchlistSeries().remove(serie);
            userRepository.save(user);
        }

        //seek relation user_season
        Optional<UsersWatchedSeason> relationUserSeason = usersWatchedSeasonRepository.findByTmdbIdAndUserId(
                userWatchedTvSeasonAddDto.getTvSeasonid(),
                user.getId()
        );
        Optional<Season> seasonToAdd = serie
                .getHasSeason()
                .stream()
                .filter(season -> season.getTmdbSeasonId().equals(userWatchedTvSeasonAddDto.getTvSeasonid()))
                .findFirst();
        // create season if required
        if (relationUserSeason.isEmpty()) {


            // add season to user season's count
            if (!user.getWatchedSeasons().contains(seasonToAdd.get())) {
                user.getWatchedSeasons().add(seasonToAdd.get());
                userRepository.save(user);

            }

            // seek new relation user_season and update status
            Optional<UsersWatchedSeason> relationUserSeasonWhenCreated = usersWatchedSeasonRepository.findByTmdbIdAndUserId(
                    userWatchedTvSeasonAddDto.getTvSeasonid(),
                    user.getId()
            );

            if (relationUserSeasonWhenCreated.isPresent()) {
                returnedStatus = Status.SEEN;
                relationUserSeasonWhenCreated.get().setStatus(Status.SEEN);
                usersWatchedSeasonRepository.save(relationUserSeasonWhenCreated.get());
                updateSerieStatus(user, serie.getTmdbId());
            }

            // create relation user_episode for every ep of the season
            createOrIncreaseUserWatchedEpisodeRelation(userWatchedTvSeasonAddDto, user);
        } else {
            // update relation to SEEN + compteur de vues
            relationUserSeason.get().setStatus(Status.SEEN);
            returnedStatus = Status.SEEN;
            relationUserSeason.get().setWatchedNumber(relationUserSeason.get().getWatchedNumber() + 1L);

            usersWatchedSeasonRepository.save(relationUserSeason.get());

            updateSerieStatus(user, serie.getTmdbId());

            createOrIncreaseUserWatchedEpisodeRelation(userWatchedTvSeasonAddDto, user);


        }
        List<Episode> episodes = new ArrayList<>(seasonToAdd.get().getHasEpisode());
        for (Episode episode : episodes) {
            increaseDurationSerieDto increaseDurationSerieDto = new increaseDurationSerieDto();
            increaseDurationSerieDto.setUsername(userWatchedTvSeasonAddDto.getUserMail());
            increaseDurationSerieDto.setSeasonNumber(seasonToAdd.get().getSeason_number());
            increaseDurationSerieDto.setEpisodeNumber(episode.getEpisode_number());
            increaseDurationSerieDto.setTvTmdbId(userWatchedTvSeasonAddDto.getTvTmdbId());
            try {
                if (checkIfEpisodeOnAir(increaseDurationSerieDto)) {
                    increaseWatchedDurationSeries(increaseDurationSerieDto);
                }

            } catch (URISyntaxException e) {
                System.out.println("error in addSeasonInWatchlist");
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("error in addSeasonInWatchlist");
                System.out.println(e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("error in addSeasonInWatchlist");
                System.out.println(e.getMessage());
            }
        }
        return returnedStatus;
    }

    @Async
    public void createOrIncreaseUserWatchedEpisodeRelation(UserWatchedTvSeasonAddDto userWatchedTvSeasonAddDto, User user) {

        seasonHasEpisodeRepository.findBySeasonImdbId(userWatchedTvSeasonAddDto.getTvSeasonid())
                .forEach(seasonHasEpisode -> {
                    Episode episode = seasonHasEpisode.getEpisode();
                    if (!user.getWatchedEpisodes().contains(episode)) {
                        increaseDurationSerieDto increaseDurationSerieDto = new increaseDurationSerieDto();
                        increaseDurationSerieDto.setUsername(userWatchedTvSeasonAddDto.getUserMail());
                        increaseDurationSerieDto.setSeasonNumber(seasonHasEpisode.getSeason().getSeason_number());
                        increaseDurationSerieDto.setEpisodeNumber(episode.getEpisode_number());
                        increaseDurationSerieDto.setTvTmdbId(userWatchedTvSeasonAddDto.getTvTmdbId());
                        try {
                            if (checkIfEpisodeOnAir(increaseDurationSerieDto)) {
                                user.getWatchedEpisodes().add(episode);
                                userRepository.save(user);
                            }
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        // increase watch_number
                        Optional<UsersWatchedEpisode> usersWatchedEpisode = usersWatchedEpisodeRepository.findByEpisodeImdbIdAndUserId(episode.getImbd_id(), user.getId());
                        usersWatchedEpisode.get().setWatchedNumber(usersWatchedEpisode.get().getWatchedNumber() + 1L);
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

        return statusDto;
    }

    public void removeMovieInWatchlist(UserWatchedMovieAddDto userWatchedMovieAddDto) throws URISyntaxException, IOException, InterruptedException {
        Movie movie = movieService.getMovieOrCreateIfNotExist(userWatchedMovieAddDto.getTmdbId(),
                userWatchedMovieAddDto.getMovieName());
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        Movie movieToRemove = movieRepository.findByTmdbId(userWatchedMovieAddDto.getTmdbId()).orElseThrow(() -> new IllegalStateException(basicErrorMessage));
        Long movieId = movieToRemove.getId();
        Long userId = user.getId();
        UsersWatchedMovie completeUserWatched = usersWatchedMovieRepository.findByMovieAndUserId(movieId, userId).orElse(null);
        multiplicatorTime = completeUserWatched != null ? completeUserWatched.getWatchedNumber().intValue() : 1;
        this.decreaseWatchedNumber(userWatchedMovieAddDto);
        this.decreaseTotalMovieWatchedTime(userWatchedMovieAddDto);
        user
                .getWatchedMovies()
                .remove(movie);
        userRepository.save(user);
        trophyService.checkAllTrophys(userWatchedMovieAddDto.getUserMail(), movie.getId(), TrophyActionName.REMOVE_MOVIE_IN_WATCHED_LIST);
    }

    public void increaseWatchedNumber(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        if (user == null) {
            throw new IllegalStateException(UserNotFound);
        }
        user.setTotalMovieWatchedNumber(user.getTotalMovieWatchedNumber() + 1);
        userRepository.save(user);
    }

    public void increaseWatchedNumberSeries(UserWatchedSerieAddDto userWatchedSerieAddDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedSerieAddDto.getUserMail());
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        if (user == null) {
            throw new IllegalStateException(UserNotFound);
        }
        user.setTotalSeriesWatchedNumber(user.getTotalSeriesWatchedNumber() + 1L);
        userRepository.save(user);
    }

    public void decreaseWatchedNumberSeries(UserWatchedSerieAddDto userWatchedSerieAddDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedSerieAddDto.getUserMail());
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        if (user == null) {
            throw new IllegalStateException(UserNotFound);
        }
        user.setTotalSeriesWatchedNumber(user.getTotalSeriesWatchedNumber() - 1L);
        if(user.getTotalSeriesWatchedNumber() < 0L) {
            user.setTotalSeriesWatchedNumber(0L);
        }
        userRepository.save(user);
    }

    public void increaseWatchedNumberEpisode(UserWatchedTvEpisodeAddDto userWatchedTvEpisodeAddDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedTvEpisodeAddDto.getUserMail());
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        if (user == null) {
            throw new IllegalStateException(UserNotFound);
        }
        user.setTotalEpisodesWatchedNumber(user.getTotalEpisodesWatchedNumber() + 1L);
        userRepository.save(user);
    }

    public void decreaseWatchedNumber(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        User user = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail()).orElseThrow(() -> new IllegalStateException(UserNotFound));
        Long movieId = movieRepository.findByTmdbId(userWatchedMovieAddDto.getTmdbId()).orElseThrow(() -> new IllegalStateException(basicErrorMessage)).getId();
        Long userId = user.getId();
        UsersWatchedMovie completeUserWatched = usersWatchedMovieRepository.findByMovieAndUserId(movieId, userId)
                .orElseThrow(() -> {
                    throw new IllegalStateException(UserNotFound);
                });
        user.setTotalMovieWatchedNumber(user.getTotalMovieWatchedNumber() - completeUserWatched.getWatchedNumber());
        if (user.getTotalMovieWatchedNumber() < 0) {
            user.setTotalMovieWatchedNumber(0L);
        }
        userRepository.save(user);
    }

    public void increaseTotalMovieWatchedTime(UserWatchedMovieAddDto userWatchedMovieAddDto) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String urlToCall = "https://api.themoviedb.org/3/movie/" + userWatchedMovieAddDto.getTmdbId() + "?api_key=" + this.apiKey;
        HttpRequest getKeywordsFromCurrentFavMovieRequest = HttpRequest.newBuilder()
                .uri(new URI(urlToCall))
                .GET()
                .build();
        HttpResponse response = client.send(getKeywordsFromCurrentFavMovieRequest, HttpResponse.BodyHandlers.ofString());
        JSONObject documentObj = new JSONObject(response.body().toString());
        Gson gson = new Gson();
        SearchSingleMovieApiDto result_search = gson.fromJson(String.valueOf(documentObj), SearchSingleMovieApiDto.class);
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        if (user == null) {
            throw new IllegalStateException(UserNotFound);
        }
        System.out.println(result_search.getTitle());
        System.out.println(result_search.getRuntime());
        System.out.println(response.body().toString());
        System.out.println(urlToCall);
        System.out.println(this.apiKey);
        Long newWatchedTotalTime = user.getTotalMovieWatchedTime().getSeconds() + Duration.ofSeconds(result_search.getRuntime() * 60L).getSeconds();
        user.setTotalMovieWatchedTime(Duration.ofSeconds(newWatchedTotalTime));

        userRepository.save(user);
    }

    public void decreaseTotalMovieWatchedTime(UserWatchedMovieAddDto userWatchedMovieAddDto) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String urlToCall = "https://api.themoviedb.org/3/movie/" + userWatchedMovieAddDto.getTmdbId() + "?api_key=" + this.apiKey;
        HttpRequest getKeywordsFromCurrentFavMovieRequest = HttpRequest.newBuilder()
                .uri(new URI(urlToCall))
                .GET()
                .build();
        HttpResponse response = client.send(getKeywordsFromCurrentFavMovieRequest, HttpResponse.BodyHandlers.ofString());
        JSONObject documentObj = new JSONObject(response.body().toString());
        Gson gson = new Gson();
        SearchSingleMovieApiDto result_search = gson.fromJson(String.valueOf(documentObj), SearchSingleMovieApiDto.class);
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        if (user == null) {
            throw new IllegalStateException(UserNotFound);
        }
        Long newWatchedTotalTime = user.getTotalMovieWatchedTime().getSeconds() - Duration.ofSeconds(result_search.getRuntime() * 60L).getSeconds() * multiplicatorTime;
        user.setTotalMovieWatchedTime(Duration.ofSeconds(newWatchedTotalTime));

        if(user.getTotalMovieWatchedTime().getSeconds() < 0){
            user.setTotalMovieWatchedTime(Duration.ofSeconds(0L));
        }
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
        s3client.deleteObject(this.bucketName, fileName);
        file.transferTo(new File(basePath + tempPathName + "original_" + fileName));
        File originalFile = new File(basePath + tempPathName + "original_" + fileName);
        File fileToUpload = new File(basePath + tempPathName + fileName);
        BufferedImage originalImage = ImageIO.read(originalFile);
        File output = fileToUpload;
        originalImage = this.removeAlphaChannel(originalImage);
        long size = originalFile.length();
        float quality = 0.0f;
        if (size < 3145728) {
            quality = 1.0f;
        }
        if (size > 3145728 && size < 5242880) {
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
        String key = s3client.getObject(this.bucketName, fileName).getKey();
        String url = s3client.getUrl(this.bucketName, key).toString();
        user.setProfilePicture(url + "?" + System.currentTimeMillis());
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
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        if (user == null) {
            throw new IllegalStateException(UserNotFound);
        }
        if (!user.getTokenResetPassword().equals(null)) {
            boolean delayCheck = user.getDateLastMailingResetPassword().plusMinutes(1).isBefore(LocalDateTime.now());
            if (!delayCheck) {
                return 403;
            }
        }
        if (user.getDateLastMailingResetPassword() != null) {
            boolean delayCheck = user.getDateLastMailingResetPassword().plusMinutes(5).isBefore(LocalDateTime.now());
            if (!delayCheck) {
                return 405;
            }
        }
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
        String token = JWT.create()
                .withIssuer("auth0")
                .sign(algorithm);
        JavaMailSender MailerService = this.getJavaMailSender();
        MimeMessage mimeMessage = MailerService.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        String htmlMsg = "<h3>Hello " + ResetPasswordMailingDto.getUsername() + " . </h3> \n You have asked to reset your password. Please click on the link below to reset your password: \n <a href=\"" + resetPasswordUrl + token + "\">Reset password</a>";
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
        } catch (JWTVerificationException exception) {
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
        try {
            if (checkToken && user.getTokenResetPassword().equals(resetPasswordUseDto.getToken())) {
                user.setPassword(passwordEncoder.encode(resetPasswordUseDto.getPassword()));
                user.setTokenResetPassword(null);
                userRepository.saveAndFlush(user);
                return 200;
            }
        } catch (Exception e) {
            return 401;
        }

        return 401;
    }

    public ProfileLazyUserDtoHeader getProfileHeaderData(String email) throws URISyntaxException, IOException, InterruptedException {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        ProfileLazyUserDtoHeader profileLazyUserDtoHeader = new ProfileLazyUserDtoHeader();
        profileLazyUserDtoHeader.setNumberOfWatchedSeries(user.getTotalSeriesWatchedNumber());
        profileLazyUserDtoHeader.setNumberOfWatchedMovies(user.getTotalMovieWatchedNumber());
        String totalDurationMoviesMonthDayHour = durationConvertor(user.getTotalMovieWatchedTime());
        profileLazyUserDtoHeader.setTotalTimeWatchedMovies(totalDurationMoviesMonthDayHour);
        String totalDurationSeriesMonthDayHour = durationConvertor(user.getTotalSeriesWatchedTime());
        profileLazyUserDtoHeader.setTotalTimeWatchedSeries(totalDurationSeriesMonthDayHour);
        return profileLazyUserDtoHeader;
    }

    public String durationConvertor(Duration duration) {
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
        long[] favoriteMoviesIds = new long[user.getFavoriteMovies().size() >= 10 ? 10 : user.getFavoriteMovies().size()];
        int i = 0;
        for (Movie movie : user.getFavoriteMovies()) {
            if (i == 10) {
                break;
            }
            favoriteMoviesIds[i] = movie.getTmdbId();
            i++;
        }
        long[] watchlistMoviesIds = new long[user.getWatchlistMovies().size() >= 10 ? 10 : user.getWatchlistMovies().size()];
        i = 0;
        for (Movie movie : user.getWatchlistMovies()) {
            if (i == 10) {
                break;
            }
            watchlistMoviesIds[i] = movie.getTmdbId();
            i++;
        }
        profileLazyUserDtoLastWatchedMovies.setLastWatchedMovies(Arrays.stream(lastWatchedMoviesIds.isPresent() ? lastWatchedMoviesIds.get() : null).limit(10).toArray());
        profileLazyUserDtoLastWatchedMovies.setFavoritesMovies(favoriteMoviesIds);
        profileLazyUserDtoLastWatchedMovies.setWatchlistMovies(watchlistMoviesIds);
        profileLazyUserDtoLastWatchedMovies.setTotalFavoritesMovies(user.getFavoriteMovies().size());
        profileLazyUserDtoLastWatchedMovies.setTotalWatchedMovies(lastWatchedMoviesIds.isPresent() ? lastWatchedMoviesIds.get().length : 0);
        profileLazyUserDtoLastWatchedMovies.setTotalWatchlistMovies(user.getWatchlistMovies().size());
        return profileLazyUserDtoLastWatchedMovies;
    }


    public ProfileLazyUserDtoSocialInfos getProfileSocialInfos(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        ProfileLazyUserDtoSocialInfos profileLazyUserDtoSocialInfos = new ProfileLazyUserDtoSocialInfos();
        profileLazyUserDtoSocialInfos.setFollowersCounter((long) user.getFollowers().size());
        profileLazyUserDtoSocialInfos.setFollowingsCounter((long) user.getFollowing().size());
        profileLazyUserDtoSocialInfos.setCommentsCounter((long) user.getComments().size());
        return profileLazyUserDtoSocialInfos;


    }

    public ProfileLazyUserDtoAvatar getProfileAvatar(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        ProfileLazyUserDtoAvatar profileLazyUserDtoAvatar = new ProfileLazyUserDtoAvatar();
        profileLazyUserDtoAvatar.setProfilePicture(user.getProfilePicture() == null ? "" : user.getProfilePicture());
        profileLazyUserDtoAvatar.setBackgroundPicture(user.getBackgroundPicture() == null ? "" : user.getBackgroundPicture());
        profileLazyUserDtoAvatar.setFullName(user.getFullName());
        profileLazyUserDtoAvatar.setFirstName(user.getFirstName());
        profileLazyUserDtoAvatar.setLastName(user.getLastName());
        profileLazyUserDtoAvatar.setNotification_system_status(user.getIsNotificationsActive());
        return profileLazyUserDtoAvatar;
    }


    public UploadBackgroundDtoResponse uploadBackgroundPic(String email, @RequestParam("file") MultipartFile file) throws IOException {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        ("user with id " + email + "does not exists")));
        if (file.isEmpty()) {
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
        s3client.deleteObject(this.bucketName, fileName);
        file.transferTo(new File(basePath + tempPathName + "original_" + fileName));
        File originalFile = new File(basePath + tempPathName + "original_" + fileName);

        File fileToUpload = new File(basePath + tempPathName + fileName);
        BufferedImage originalImage = ImageIO.read(originalFile);
        File output = fileToUpload;
        originalImage = this.removeAlphaChannel(originalImage);
        long size = originalFile.length();
        float quality = 0.0f;
        if (size < 3145728) {
            quality = 1.0f;
        }
        if (size > 3145728 && size < 5242880) {
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
        String key = s3client.getObject(this.bucketName, fileName).getKey();
        String url = s3client.getUrl(this.bucketName, key).toString();
        user.setBackgroundPicture(url + "?" + System.currentTimeMillis());
        userRepository.save(user);
        if (fileToUpload.delete()) {
            LOGGER.print("File deleted successfully");
        }
        if (originalFile.delete()) {
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
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        if (user == null) {
            throw new IllegalStateException(UserNotFound);
        }
        if (!user.getFavoriteMovies().contains(movie)) {
            user
                    .getFavoriteMovies()
                    .add(movie);
            userRepository.save(user);
            return true;
        }
        if (user.getFavoriteMovies().contains(movie)) {
            user
                    .getFavoriteMovies()
                    .remove(movie);
            userRepository.save(user);
            return false;
        }
        return false;
    }

    public boolean toggleTvInFavoritelist(UserWatchedSerieAddDto userWatchedSerieAddDto) throws IOException, URISyntaxException, InterruptedException {
        Serie serie = this.tvService.getSerieOrCreateIfNotExist(userWatchedSerieAddDto.getTmdbId());

        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedSerieAddDto.getUserMail());
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        if (user == null) {
            throw new IllegalStateException(UserNotFound);
        }
        if (!user.getFavoriteSeries().contains(serie)) {
            user
                    .getFavoriteSeries()
                    .add(serie);
            userRepository.save(user);
            trophyService.checkAllTrophys(user.getUsername(), serie.getId(), TrophyActionName.ADD_SERIE_IN_WATCHED_LIST);
            return true;
        }
        if (user.getFavoriteSeries().contains(serie)) {
            user
                    .getFavoriteSeries()
                    .remove(serie);
            userRepository.save(user);
            trophyService.checkAllTrophys(user.getUsername(), serie.getId(), TrophyActionName.REMOVE_SERIE_IN_WATCHED_LIST);
            return false;
        }
        return false;
    }

    public boolean toggleTvInWatchlist(UserWatchedSerieAddDto userWatchedSerieAddDto) throws IOException, URISyntaxException, InterruptedException {
        Serie serie = this.tvService.getSerieOrCreateIfNotExist(userWatchedSerieAddDto.getTmdbId());

        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedSerieAddDto.getUserMail());
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        if (user == null) {
            throw new IllegalStateException(UserNotFound);
        }
        if (user.getWatchlistSeries().contains(serie) || user.getWatchedSeries().contains(serie)) {
            user
                    .getWatchlistSeries()
                    .remove(serie);
            userRepository.save(user);
            return false;
        }
        if (!user.getWatchlistSeries().contains(serie)) {
            user
                    .getWatchlistSeries()
                    .add(serie);
            userRepository.save(user);
            return true;
        }
        if (user.getWatchlistSeries().contains(serie)) {
            user
                    .getWatchlistSeries()
                    .remove(serie);
            userRepository.save(user);
            return false;
        }
        return false;
    }


    public boolean toggleMovieInMovieToWatchlist(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        Movie movie = movieService.getMovieOrCreateIfNotExist(userWatchedMovieAddDto.getTmdbId(),
                userWatchedMovieAddDto.getMovieName());

        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        if (user == null) {
            throw new IllegalStateException(UserNotFound);
        }
        if (!user.getWatchlistMovies().contains(movie)) {
            user
                    .getWatchlistMovies()
                    .add(movie);
            userRepository.save(user);
            return true;
        }
        if (user.getWatchlistMovies().contains(movie)) {
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

    public boolean isTvInFavoritelist(UserWatchedSerieAddDto userWatchedSerieAddDto) {
        Optional<UserSimpleDto> user = userRepository.isTvInFavorite(
                userWatchedSerieAddDto.getUserMail(), userWatchedSerieAddDto.getTmdbId());
        return user.isPresent();
    }

    public boolean isTvInWatchlistSeries(UserWatchedSerieAddDto userWatchedSerieAddDto) {
        Optional<UserSimpleDto> user = userRepository.isTvInWatchlistSeries(
                userWatchedSerieAddDto.getUserMail(), userWatchedSerieAddDto.getTmdbId());
        return user.isPresent();
    }


    public fetchRangeListDto lastWatchedMoviesRange(fetchRangeDto fetchRangeDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(fetchRangeDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        Optional<long[]> lastWatchedMoviesIds = usersWatchedMovieRepository.findWatchedMoviesByUserId(user.getId());
        long[] stream = lastWatchedMoviesIds.isPresent() ? lastWatchedMoviesIds.get() : new long[0];
        long[] listToUse = Arrays.stream(stream)
                .skip(fetchRangeDto.getCurrentLength())
                .limit(fetchRangeDto.getCurrentLength() + 10L)
                .toArray();
        fetchRangeListDto fetchRangeListDto = new fetchRangeListDto();
        fetchRangeListDto.setTmdbIdList(listToUse);
        return fetchRangeListDto;
    }

    public ArrayList<Long> fetchfavoritesSeries(UserMailDto userMailDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(userMailDto.getUserMail());
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        ArrayList<Long> seriesIds = new ArrayList<>();
        if (optionalUser.isPresent()) {
            Serie[] series = Arrays.stream(user.getFavoriteSeries().toArray())
                    .limit(10L)
                    .toArray(Serie[]::new);
            for (Serie serie : series) {
                seriesIds.add(serie.getTmdbId());
            }
            return seriesIds;
        } else {
            throw new IllegalStateException(UserNotFound);
        }
    }

    public ArrayList<Long> fetchTvWatchlist(UserMailDto userMailDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(userMailDto.getUserMail());
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        ArrayList<Long> seriesIds = new ArrayList<>();
        if (optionalUser.isPresent()) {
            Serie[] series = Arrays.stream(user.getWatchlistSeries().toArray())
                    .limit(10L)
                    .toArray(Serie[]::new);
            for (Serie serie : series) {
                seriesIds.add(serie.getTmdbId());
            }
            return seriesIds;
        } else {
            throw new IllegalStateException(UserNotFound);
        }
    }

    public fetchRangeListDto favoritesMoviesRange(fetchRangeDto fetchRangeDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(fetchRangeDto.getUserMail());
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        if (user == null) {
            throw new IllegalStateException(UserNotFound);
        }
        Movie[] listRange = Arrays.stream(user.getFavoriteMovies().toArray())
                .skip(fetchRangeDto.getCurrentLength())
                .limit(fetchRangeDto.getCurrentLength() + 10L)
                .toArray(Movie[]::new);
        return getFetchRangeListDto(listRange);
    }

    public fetchRangeListDto watchlistMoviesRange(fetchRangeDto fetchRangeDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(fetchRangeDto.getUserMail());
        User user = optionalUser.isPresent() ? optionalUser.get() : null;
        if (user == null) {
            throw new IllegalStateException(UserNotFound);
        }
        Movie[] listRange = Arrays.stream(user.getWatchlistMovies().toArray())
                .skip(fetchRangeDto.getCurrentLength())
                .limit(fetchRangeDto.getCurrentLength() + 10L)
                .toArray(Movie[]::new);
        return getFetchRangeListDto(listRange);
    }

    private fetchRangeListDto getFetchRangeListDto(Movie[] listRange) {
        long[] listToUse = new long[listRange.length];
        int i = 0;
        for (Movie movie : listRange) {
            listToUse[i] = movie.getTmdbId();
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

    public Optional<User> findOneUserByEmailOrCreateIt(GoogleIdToken.Payload payload) throws JsonProcessingException {
        String email = payload.getEmail();
        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");
        String locale = (String) payload.get("locale");
        String familyName = (String) payload.get("family_name");
        String givenName = (String) payload.get("given_name");
        Optional<User> user = this.userRepository.findUserByEmail(email);
        if (user.isPresent()) {
            return user;
        }
        if (!user.isPresent()) {
            String newPassword = UUID.randomUUID().toString();
            RegisterGoogleDto registerDto = new RegisterGoogleDto();
            registerDto.setUsername(email);
            registerDto.setPassword(newPassword);
            registerDto.setFirstName(givenName);
            registerDto.setLastName(familyName);
            this.userAuthService.registerGoogleSignin(registerDto);
        }

        return this.userRepository.findUserByEmail(email);
    }

    public void saveUser(User user) {
        this.userRepository.save(user);
    }

    public SocialInfoDto getSocialPageInfo(String email) {
        User user = this.userRepository.findUserByEmail(email).orElseThrow(() -> new IllegalStateException(UserNotFound));
        Set<Trophy> trophies = trophyRepository.findAllbyUser(user);
        SocialInfoDto socialInfoDto = new SocialInfoDto();
        socialInfoDto.setAbout(user.getAbout());
        socialInfoDto.setComments("No comments yet");
        socialInfoDto.setTrophies(trophies);
        return socialInfoDto;
    }

    public SocialSearchResponseDto[] searchUser(String searchText) {
        User[] usersFound = this.userRepository.searchUser(searchText);
        SocialSearchResponseDto[] socialSearchResponseDtos = new SocialSearchResponseDto[usersFound.length];
        int i = 0;
        for (User user : usersFound) {
            SocialSearchResponseDto socialSearchResponseDto = new SocialSearchResponseDto();
            socialSearchResponseDto.setUsername(user.getUsername());
            socialSearchResponseDto.setFullName(user.getFirstName() + " " + user.getLastName());
            socialSearchResponseDto.setProfilePicture(user.getProfilePicture());
            socialSearchResponseDto.setScore(0);
            socialSearchResponseDtos[i] = socialSearchResponseDto;
            i++;
        }
        return socialSearchResponseDtos;
    }

    public SocialTopTenUserDto[] getTopTenUsers() {
        User[] usersFound = Arrays.stream(this.userRepository.getTopTen()).limit(10).toArray(User[]::new);
        SocialTopTenUserDto[] SocialTopTenUserDtos = new SocialTopTenUserDto[usersFound.length];
        int i = 0;
        for (User user : usersFound) {
            SocialTopTenUserDto socialTopTenUserDto = new SocialTopTenUserDto();
            socialTopTenUserDto.setFullName(user.getFirstName() + " " + user.getLastName());
            socialTopTenUserDto.setUsername(user.getUsername());
            socialTopTenUserDto.setProfilePicture(user.getProfilePicture());
            socialTopTenUserDto.setScore(user.getTrophy().size());
            socialTopTenUserDto.setRank(i + 1);
            SocialTopTenUserDtos[i] = socialTopTenUserDto;
            i++;
        }
        return SocialTopTenUserDtos;
    }

    public SocialInfoDto getSocialDetail(String email) {
        User user = this.userRepository.findUserByEmail(email).orElseThrow(() -> new IllegalStateException(UserNotFound));
        SocialInfoDto socialInfoDto = new SocialInfoDto();
        socialInfoDto.setAbout(user.getAbout());
        socialInfoDto.setComments("No comments yet");
        socialInfoDto.setTrophies(user.getTrophy());
        return socialInfoDto;
    }

    public void excludeActor(Long idActor, long idUser) {
        Optional<User> userOptional = userRepository.findById(idUser);
        User user = userOptional.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });

        Set<Actor> excludedActorIdFromRecommended = user.getExcludedActorIdFromRecommended();

        Optional<Actor> actor = actorRepository.findByTmdbId(idActor);

        Actor newActor = null;
        if (actor.isEmpty()) {
            newActor = actorRepository.saveAndFlush(new Actor(idActor));
        }
        excludedActorIdFromRecommended.add(actor.orElse(newActor));

        userRepository.saveAndFlush(user);
    }

    public void excludeGenre(Long idGenre, Long idUser) {
        Optional<User> userOptional = userRepository.findById(idUser);
        User user = userOptional.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });

        Set<Genre> excludedGenreIdFromRecommended = user.getExcludedGenreIdFromRecommended();

        Optional<Genre> genre = genreRepository.findByTmdbId(idGenre);

        Genre newGenre = null;
        if (genre.isEmpty()) {
            newGenre = genreRepository.saveAndFlush(new Genre(idGenre));
        }
        excludedGenreIdFromRecommended.add(genre.orElse(newGenre));

        userRepository.saveAndFlush(user);
    }

    public SocialFollowingResponseDto getFollowingStatus(SocialFollowingRequestDto information) {
        User user = userRepository.findUserByEmail(information.getUsernameRequester()).orElseThrow(() -> new IllegalStateException(UserNotFound));
        User userToFollow = userRepository.findUserByEmail(information.getUsernameRequested()).orElseThrow(() -> new IllegalStateException(UserNotFound));
        SocialFollowingResponseDto socialFollowingResponseDto = new SocialFollowingResponseDto();
        socialFollowingResponseDto.setFollowing(user.getFollowing().contains(userToFollow));
        return socialFollowingResponseDto;
    }

    public SocialFollowingResponseDto actionFollowUser(SocialFollowingRequestDto information) {
        User user = userRepository.findUserByEmail(information.getUsernameRequester()).orElseThrow(() -> new IllegalStateException(UserNotFound));
        User userToFollow = userRepository.findUserByEmail(information.getUsernameRequested()).orElseThrow(() -> new IllegalStateException(UserNotFound));
        SocialFollowingResponseDto socialFollowingResponseDto = new SocialFollowingResponseDto();
        socialFollowingResponseDto.setFollowing(false);
        if (!user.getFollowing().contains(userToFollow)) {
            userToFollow.getFollowers().add(user);
            userRepository.saveAndFlush(userToFollow);
            socialFollowingResponseDto.setFollowing(true);
            Notification notification = new Notification();
            notification.setType("follow");
            notification.setSeverity("info");
            notification.setMessage(user.getFirstName() + " " + user.getLastName() + " started following you");
            this.notificationToUser(userToFollow.getUsername(), notification);
        }
        return socialFollowingResponseDto;
    }

    public SocialFollowingResponseDto actionUnfollowUser(SocialFollowingRequestDto information) {
        User user = userRepository.findUserByEmail(information.getUsernameRequester()).orElseThrow(() -> new IllegalStateException(UserNotFound));
        User userToFollow = userRepository.findUserByEmail(information.getUsernameRequested()).orElseThrow(() -> new IllegalStateException(UserNotFound));
        SocialFollowingResponseDto socialFollowingResponseDto = new SocialFollowingResponseDto();
        socialFollowingResponseDto.setFollowing(true);
        if (user.getFollowing().contains(userToFollow)) {
            userToFollow.getFollowers().remove(user);
            userRepository.saveAndFlush(userToFollow);
            socialFollowingResponseDto.setFollowing(false);
        }
        return socialFollowingResponseDto;
    }

    public boolean notificationToUser(String email, Notification notification) {
        String topicName = this.ENV + "UserNotificationService";
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new IllegalStateException(UserNotFound));
        user.getNotifications().add(notification);
        userRepository.saveAndFlush(user);
        this.kafkaMessageGeneratorService.sendNotification(user, notification, topicName);
        return true;
    }

    public Set<Notification> getUserNotification(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new IllegalStateException(UserNotFound));
        return user.getNotifications();
    }

    public boolean updateUserNotification(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new IllegalStateException(UserNotFound));

        user.getNotifications()
                .stream()
                .filter(notification -> notification.getStatus() == NotificationStatus.UNREAD)
                .forEach(notification -> notification.setStatus(NotificationStatus.READ));
        userRepository.saveAndFlush(user);
        return true;
    }

    public UploadPictureDtoResponse uploadProfilePicTempForCrop(String email, @RequestParam("file") MultipartFile file) throws IOException {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        ("user with id " + email + "does not exists")));
        String fileName = user.getId() + "_profile_pic_temp_for_crop." + file.getOriginalFilename().split("\\.")[1];
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
        s3client.deleteObject(this.bucketName, fileName);
        file.transferTo(new File(basePath + tempPathName + "original_" + fileName));
        File originalFile = new File(basePath + tempPathName + "original_" + fileName);
        File fileToUpload = new File(basePath + tempPathName + fileName);
        BufferedImage originalImage = ImageIO.read(originalFile);
        File output = fileToUpload;
        originalImage = this.removeAlphaChannel(originalImage);
        long size = originalFile.length();
        float quality = 0.0f;
        if (size < 3145728) {
            quality = 1.0f;
        }
        if (size > 3145728 && size < 5242880) {
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
        String key = s3client.getObject(this.bucketName, fileName).getKey();
        String url = s3client.getUrl(this.bucketName, key).toString();
        user.setProfilePictureTempForCrop(url + "?" + System.currentTimeMillis());
        userRepository.save(user);
        fileToUpload.delete();
        originalFile.delete();
        UploadPictureDtoResponse uploadPictureDtoResponse = new UploadPictureDtoResponse();
        uploadPictureDtoResponse.setNewPictureUrl(url);
        return uploadPictureDtoResponse;
    }

    public ProfileLazyUserDtoAvatar getTempForCropUrl(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        ProfileLazyUserDtoAvatar profileLazyUserDtoAvatar = new ProfileLazyUserDtoAvatar();
        profileLazyUserDtoAvatar.setProfilePicture(user.getProfilePictureTempForCrop() == null ? "" : user.getProfilePictureTempForCrop());
        profileLazyUserDtoAvatar.setBackgroundPicture(user.getBackgroundPictureTempForCrop() == null ? "" : user.getBackgroundPictureTempForCrop());
        profileLazyUserDtoAvatar.setFullName(user.getFullName());
        return profileLazyUserDtoAvatar;
    }

    public UploadBackgroundDtoResponse uploadBackgroundPicTempForCrop(String email, @RequestParam("file") MultipartFile file) throws IOException {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        ("user with id " + email + "does not exists")));
        if (file.isEmpty()) {
            throw new IllegalStateException("File is empty");
        }
        String fileName = user.getId() + "_background_pic_temp_for_crop." + file.getOriginalFilename().split("\\.")[1];
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
        s3client.deleteObject(this.bucketName, fileName);
        file.transferTo(new File(basePath + tempPathName + "original_" + fileName));
        File originalFile = new File(basePath + tempPathName + "original_" + fileName);

        File fileToUpload = new File(basePath + tempPathName + fileName);
        BufferedImage originalImage = ImageIO.read(originalFile);
        File output = fileToUpload;
        originalImage = this.removeAlphaChannel(originalImage);
        long size = originalFile.length();
        float quality = 0.0f;
        if (size < 3145728) {
            quality = 1.0f;
        }
        if (size > 3145728 && size < 5242880) {
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
        String key = s3client.getObject(this.bucketName, fileName).getKey();
        String url = s3client.getUrl(this.bucketName, key).toString();
        user.setBackgroundPictureTempForCrop(url + "?" + System.currentTimeMillis());
        userRepository.save(user);
        if (fileToUpload.delete()) {
            LOGGER.print("File deleted successfully");
        }
        if (originalFile.delete()) {
            LOGGER.print("File deleted successfully");
        }
        UploadBackgroundDtoResponse uploadBackgroundDtoResponse = new UploadBackgroundDtoResponse();
        uploadBackgroundDtoResponse.setNewBackgroundUrl(url);
        return uploadBackgroundDtoResponse;
    }

    public boolean isEpisodeInWatchlist(UserWatchedEpisodeDto userWatchedEpisodeDto) {
        Optional<UsersWatchedEpisode> seen = userRepository.isEpisodeWatched(
                userWatchedEpisodeDto.getUserMail(),
                userWatchedEpisodeDto.getEpisodeTmdbId()
        );
        if (seen.isPresent()) {
            return true;
        } else {
            return false;
        }

    }

    public Status isSeasonInWatchlist(UserWatchedTvSeasonAddDto userWatchedTvSeasonAddDto) {
        Status status = Status.NOTSEEN;
        if (userWatchedTvSeasonAddDto.getTvSeasonid() == null) {
            return status;
        }
        Optional<UsersWatchedSeason> seasonStatus = userRepository.getSeasonStatus(
                userWatchedTvSeasonAddDto.getUserMail(),
                userWatchedTvSeasonAddDto.getTvSeasonid()

        );
        if (seasonStatus.isPresent()) {
            status = seasonStatus.get().getStatus();
        }
        return status;
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
        if (watchingSeriesRelation.isPresent()) {

            for (UsersWatchedSeries usersWatchedSeries : watchingSeriesRelation.get()) {
                seriesWatching.add(usersWatchedSeries.getSerie().getTmdbId());
            }
            return seriesWatching;
        } else {
            return null;
        }
    }

    public String getUserFromJwt(Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isPresent()) {
            return user.get().getUsername();
        }
        throw new IllegalStateException("invalid JWT - User not found");
    }

    public boolean editAccountPasswordInfos(long idUser, EditPasswordDto passwordModifier) {
        Optional<User> user = userRepository.findById(idUser);
        PasswordEncoder passwordEncoder = this.encoder();
        if (user.isPresent()) {
            user.get().setPassword(passwordEncoder.encode(passwordModifier.getNewPassword()));
            userRepository.save(user.get());
            return true;
        }
        return false;
    }

    public boolean updateAboutUser(String username, String aboutYou) {
        Optional<User> user = userRepository.findUserByEmail(username);
        if (user.isPresent()) {
            user.get().setAbout(aboutYou);
            userRepository.save(user.get());
            return true;
        }
        return false;
    }

    public AboutYouResponseDto getAboutUser(String username) {
        AboutYouResponseDto aboutYouResponseDto = new AboutYouResponseDto();
        Optional<User> user = userRepository.findUserByEmail(username);
        if (user.isPresent()) {
            if (user.get().getAbout() != null) {
                aboutYouResponseDto.setAboutYou(user.get().getAbout());
            }
        }
        return aboutYouResponseDto;
    }

    public boolean getTermOfUseInformation(String username) {
        Optional<User> user = userRepository.findUserByEmail(username);
        if (user.isPresent()) {
            return user.get().getIsTermsOfUseAccepted();
        }
        return false;
    }

    public boolean acceptTermOfUseInformation(String username) {
        Optional<User> user = userRepository.findUserByEmail(username);
        if (user.isPresent()) {
            user.get().setIsTermsOfUseAccepted(true);
            userRepository.save(user.get());
            return true;
        }
        return false;
    }

    public boolean removeSerieFromViewInfo(UserWatchedSerieAddDto userWatchedSerieAddDto) throws IOException, URISyntaxException, InterruptedException {
        Serie serie = this.tvService.getSerieOrCreateIfNotExist(userWatchedSerieAddDto.getTmdbId());

        // récup l'user
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedSerieAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));

        //remove to watchlist bcz seen
        if (user.getWatchlistSeries().contains(serie)) {
            user.getWatchlistSeries().remove(serie);
            userRepository.save(user);
        }
        if (user.getWatchedSeries().contains(serie)) {
            user.getWatchedSeries().remove(serie);
            trophyService.checkAllTrophys(user.getUsername(), serie.getId(), TrophyActionName.REMOVE_SERIE_IN_WATCHED_LIST);
            serie.getHasSeason().forEach(season -> {
                if (user.getWatchedSeasons().contains(season)) {
                    user.getWatchedSeasons().remove(season);
                }
                season.getHasEpisode().forEach(episode -> {
                    if (user.getWatchedEpisodes().contains(episode)) {
                        user.getWatchedEpisodes().remove(episode);
                        increaseDurationSerieDto increaseDurationSerieDto = new increaseDurationSerieDto();
                        increaseDurationSerieDto.setTvTmdbId(serie.getTmdbId());
                        increaseDurationSerieDto.setSeasonNumber(season.getSeason_number());
                        increaseDurationSerieDto.setEpisodeNumber(episode.getEpisode_number());
                        increaseDurationSerieDto.setUsername(user.getUsername());
                        try {
                            this.decreaseWatchedDurationSeries(increaseDurationSerieDto);
                        } catch (URISyntaxException | IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            });
        }

        decreaseWatchedNumberSeries(userWatchedSerieAddDto);
        userRepository.save(user);


        return true;
    }


    public boolean removeSeasonFromViewInfo(UserRemoveSeasonDto userRemoveSeasonDto) throws IOException, URISyntaxException, InterruptedException {
        Serie serie = this.tvService.getSerieOrCreateIfNotExist(userRemoveSeasonDto.getTvTmdbId());

        // récup l'user
        Optional<User> optionalUser = userRepository.findUserByEmail(userRemoveSeasonDto.getUsername());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));

        Optional<Season> seasonToRemove = seasonRepository.findByTmdbSeasonId(userRemoveSeasonDto.getSeasonTmdbId());
        AtomicBoolean isSerieStillBeingWatched = new AtomicBoolean(false);

        if (user.getWatchedSeasons().contains(seasonToRemove.get())) {
            user.getWatchedSeasons().remove(seasonToRemove.get());
            userRepository.save(user);

            // Remove watched episodes for the season
            seasonToRemove.get().getHasEpisode().parallelStream().forEach(episode -> {
                if (user.getWatchedEpisodes().contains(episode)) {
                    user.getWatchedEpisodes().remove(episode);

                    increaseDurationSerieDto increaseDurationSerieDto = new increaseDurationSerieDto();
                    increaseDurationSerieDto.setTvTmdbId(serie.getTmdbId());
                    increaseDurationSerieDto.setSeasonNumber(seasonToRemove.get().getSeason_number());
                    increaseDurationSerieDto.setEpisodeNumber(episode.getEpisode_number());
                    increaseDurationSerieDto.setUsername(user.getUsername());

                    try {
                        this.decreaseWatchedDurationSeries(increaseDurationSerieDto);
                    } catch (URISyntaxException | IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Check if the serie is still being watched
            isSerieStillBeingWatched.set(user.getWatchedSeasons().stream()
                    .anyMatch(season -> serie.getHasSeason().contains(season)));

            userRepository.save(user);
        }

        // Remove from watchlist if present
        if (user.getWatchlistSeries().contains(serie)) {
            user.getWatchlistSeries().remove(serie);
            userRepository.save(user);
        }

        // Remove from watched series if not being watched anymore
        if (user.getWatchedSeries().contains(serie) && !isSerieStillBeingWatched.get()) {
            user.getWatchedSeries().remove(serie);
            userRepository.save(user);
            trophyService.checkAllTrophys(user.getUsername(), serie.getId(), TrophyActionName.REMOVE_SERIE_IN_WATCHED_LIST);
        }

        updateSerieStatus2(user, serie.getTmdbId());

        return true;
    }


    public boolean removeEpisodeFromViewInfo(UserRemoveEpisodeDto userRemoveEpisodeDto) throws IOException, URISyntaxException, InterruptedException {
        Serie serie = this.tvService.getSerieOrCreateIfNotExist(userRemoveEpisodeDto.getTvTmdbId());
        // récup l'user
        Optional<User> optionalUser = userRepository.findUserByEmail(userRemoveEpisodeDto.getUsername());
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        Optional<Episode> episodeToRemove = episodeRepository.findByTmdbEpisodeId(userRemoveEpisodeDto.getEpisodeTmdbId());
        Optional<Season> seasonToCheck = seasonRepository.findByTmdbSeasonId(userRemoveEpisodeDto.getSeasonTmdbId());
        AtomicBoolean isSerieStilBeingWatched = new AtomicBoolean(false);
        AtomicBoolean isSeasonStilBeingWatched = new AtomicBoolean(false);
        increaseDurationSerieDto increaseDurationSerieDto = new increaseDurationSerieDto();
        increaseDurationSerieDto.setTvTmdbId(serie.getTmdbId());
        increaseDurationSerieDto.setSeasonNumber(userRemoveEpisodeDto.getSeasonNumber());
        increaseDurationSerieDto.setEpisodeNumber(userRemoveEpisodeDto.getEpisodeNumber());
        increaseDurationSerieDto.setUsername(user.getUsername());
        if (user.getWatchedEpisodes().contains(episodeToRemove.get())) {
            user.getWatchedEpisodes().remove(episodeToRemove.get());
        }
        userRepository.save(user);
        int episodeInWatchedSeason = 0;
        int saisonLength = 0;
        for (Season season : user.getWatchedSeasons()) {
            if (season.getTmdbSeasonId().equals(userRemoveEpisodeDto.getSeasonTmdbId())) {
                saisonLength = season.getHasEpisode().size();
                for (Episode episode : season.getHasEpisode()) {
                    Episode currentEpisode = episode;
                    Set<Episode> userEpisodeList = user.getWatchedEpisodes();
                    if (user.getWatchedEpisodes().contains(episode)) {
                        episodeInWatchedSeason++;
                    }
                }
            }
        }
        if (episodeInWatchedSeason == 0) {
            user.getWatchedSeasons().remove(seasonToCheck.get());
            userRepository.save(user);
        }

        serie.getHasSeason().forEach(season1 -> {
            for (Episode episode : season1.getHasEpisode()) {
                if (user.getWatchedEpisodes().contains(episode)) {
                    isSerieStilBeingWatched.set(true);
                }
            }
            if (user.getWatchedSeasons().contains(season1)) {
                isSerieStilBeingWatched.set(true);
            }
        });
        if (user.getWatchlistSeries().contains(serie)) {
            user.getWatchlistSeries().remove(serie);
            userRepository.save(user);
        }
        if (user.getWatchedSeries().contains(serie) && !isSerieStilBeingWatched.get()) {
            user.getWatchedSeries().remove(serie);
            trophyService.checkAllTrophys(user.getUsername(), serie.getId(), TrophyActionName.REMOVE_SERIE_IN_WATCHED_LIST);
            userRepository.save(user);
        }

        updateSerieStatus2(user, serie.getTmdbId());
        this.decreaseWatchedDurationSeries(increaseDurationSerieDto);
        updateSeasonStatus2(userRemoveEpisodeDto.getSeasonTmdbId(), userRemoveEpisodeDto.getUsername(), userRemoveEpisodeDto.getTvTmdbId());

        return true;
    }

    @Async
    public void increaseWatchedDurationSeries(increaseDurationSerieDto increaseDurationSerieDto) throws URISyntaxException, IOException, InterruptedException {
        Optional<User> optionalUser = userRepository.findUserByEmail(increaseDurationSerieDto.getUsername());
        Episode episode = serieRepository.findSerieByTmdbId(increaseDurationSerieDto.getTvTmdbId()).get().getHasSeason().stream().filter(season -> season.getSeason_number().equals(increaseDurationSerieDto.getSeasonNumber())).findFirst().get().getHasEpisode().stream().filter(episode1 -> episode1.getEpisode_number().equals(increaseDurationSerieDto.getEpisodeNumber())).findFirst().get();
        if(episode.getRuntime()>0){
            Long newWatchedTotalTime = optionalUser.get().getTotalSeriesWatchedTime().getSeconds() + Duration.ofSeconds(episode.getRuntime() * 60L).getSeconds();
            optionalUser.get().setTotalSeriesWatchedTime(Duration.ofSeconds(newWatchedTotalTime));
        }
        if(episode.getRuntime() == 0){
            boolean checkCache = this.redisService.getLastCheckOnAirDateEpisode(String.valueOf(increaseDurationSerieDto.getTvTmdbId())+"-increaseWatchedDurationSeries");
            if(checkCache){
                return;
            }
            String urlToCall = "https://api.themoviedb.org/3/tv/" + increaseDurationSerieDto.getTvTmdbId() + "/season/" + increaseDurationSerieDto.getSeasonNumber() + "/episode/" + increaseDurationSerieDto.getEpisodeNumber() + "?api_key=" + this.apiKey;
            JSONObject checkInCache = this.redisService.getDataFromRedisForInternalRequest(urlToCall);
            Gson gson = new Gson();
            SearchSingleMovieApiDto result_search = gson.fromJson(String.valueOf(checkInCache), SearchSingleMovieApiDto.class);

            User user = optionalUser.isPresent() ? optionalUser.get() : null;
            if (user == null) {
                throw new IllegalStateException(UserNotFound);
            }
            Integer runtime = result_search.getRuntime();
            result_search.setRuntime(runtime != null ? runtime : 0);
            System.out.println(result_search.getTitle());
            System.out.println(result_search.getRuntime());
            System.out.println(checkInCache.toString());
            System.out.println(urlToCall);
            System.out.println(this.apiKey);
            Long newWatchedTotalTime = user.getTotalSeriesWatchedTime().getSeconds() + Duration.ofSeconds(result_search.getRuntime() * 60L).getSeconds();
            user.setTotalSeriesWatchedTime(Duration.ofSeconds(newWatchedTotalTime));

            userRepository.save(user);
        }


    }

    @Async
    public void decreaseWatchedDurationSeries(increaseDurationSerieDto increaseDurationSerieDto) throws URISyntaxException, IOException, InterruptedException {
        Optional<User> optionalUser = userRepository.findUserByEmail(increaseDurationSerieDto.getUsername());
        Episode episode = serieRepository.findSerieByTmdbId(increaseDurationSerieDto.getTvTmdbId()).get().getHasSeason().stream().filter(season -> season.getSeason_number().equals(increaseDurationSerieDto.getSeasonNumber())).findFirst().get().getHasEpisode().stream().filter(episode1 -> episode1.getEpisode_number().equals(increaseDurationSerieDto.getEpisodeNumber())).findFirst().get();
        if(episode.getRuntime()>0){
            Long newWatchedTotalTime = optionalUser.get().getTotalSeriesWatchedTime().getSeconds() + Duration.ofSeconds(episode.getRuntime() * 60L).getSeconds();
            optionalUser.get().setTotalSeriesWatchedTime(Duration.ofSeconds(newWatchedTotalTime));
        }
        if(episode.getRuntime() == 0){
            boolean checkCache = this.redisService.getLastCheckOnAirDateEpisode(String.valueOf(increaseDurationSerieDto.getTvTmdbId())+"-decreaseWatchedDurationSeries");
            if(checkCache){
                return;
            }
            String urlToCall = "https://api.themoviedb.org/3/tv/" + increaseDurationSerieDto.getTvTmdbId() + "/season/" + increaseDurationSerieDto.getSeasonNumber() + "/episode/" + increaseDurationSerieDto.getEpisodeNumber() + "?api_key=" + this.apiKey;
            JSONObject checkInCache = this.redisService.getDataFromRedisForInternalRequest(urlToCall);
            Gson gson = new Gson();
            SearchSingleMovieApiDto result_search = gson.fromJson(String.valueOf(checkInCache), SearchSingleMovieApiDto.class);
            User user = optionalUser.isPresent() ? optionalUser.get() : null;
            if (user == null) {
                throw new IllegalStateException(UserNotFound);
            }
            Integer runtime = result_search.getRuntime();
            result_search.setRuntime(runtime != null ? runtime : 0);
            System.out.println(result_search.getTitle());
            System.out.println(result_search.getRuntime());
            System.out.println(checkInCache.toString());
            System.out.println(urlToCall);
            System.out.println(this.apiKey);
            Long newWatchedTotalTime = user.getTotalSeriesWatchedTime().getSeconds() - Duration.ofSeconds(result_search.getRuntime() * 60L).getSeconds();
            if(newWatchedTotalTime < 0){
                newWatchedTotalTime = 0L;
            }
            user.setTotalSeriesWatchedTime(Duration.ofSeconds(newWatchedTotalTime));

            userRepository.save(user);
        }


    }

    public boolean checkIfEpisodeOnAir(increaseDurationSerieDto increaseDurationSerieDto) throws URISyntaxException, IOException, InterruptedException {
        Episode episode = serieRepository.findSerieByTmdbId(increaseDurationSerieDto.getTvTmdbId()).get().getHasSeason().stream().filter(season -> season.getSeason_number().equals(increaseDurationSerieDto.getSeasonNumber())).findFirst().get().getHasEpisode().stream().filter(episode1 -> episode1.getEpisode_number().equals(increaseDurationSerieDto.getEpisodeNumber())).findFirst().get();

        if(episode.getAir_date() != null){
            LocalDate today = LocalDate.now();
            LocalDate dateOnAir = LocalDate.parse(episode.getAir_date());
            return dateOnAir.isBefore(today) || dateOnAir.isEqual(today);
        }
        boolean checkCache = this.redisService.getLastCheckOnAirDateEpisode(String.valueOf(increaseDurationSerieDto.getTvTmdbId())+"-checkIfEpisodeOnAir");
        if(checkCache){
            return true;
        }
        String urlToCall = "https://api.themoviedb.org/3/tv/" + increaseDurationSerieDto.getTvTmdbId() + "/season/" + increaseDurationSerieDto.getSeasonNumber() + "/episode/" + increaseDurationSerieDto.getEpisodeNumber() + "?api_key=" + this.apiKey;
        JSONObject checkInCache = this.redisService.getDataFromRedisForInternalRequest(urlToCall);
        Gson gson = new Gson();
        AddSeasonDto result_search = gson.fromJson(String.valueOf(checkInCache), AddSeasonDto.class);
        LocalDate today = LocalDate.now();
        LocalDate dateOnAir = null;
        try {
            if (result_search.air_date != null) {
                dateOnAir = LocalDate.parse(result_search.air_date);
                this.redisService.setLastCheckOnAirDateEpisode(String.valueOf(increaseDurationSerieDto.getTvTmdbId())+"-checkIfEpisodeOnAir",dateOnAir.isBefore(today) || dateOnAir.isEqual(today));
                return dateOnAir.isBefore(today) || dateOnAir.isEqual(today);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            this.redisService.setLastCheckOnAirDateEpisode(String.valueOf(increaseDurationSerieDto.getTvTmdbId())+"-checkIfEpisodeOnAir",false);
            return false;
        }
        this.redisService.setLastCheckOnAirDateEpisode(String.valueOf(increaseDurationSerieDto.getTvTmdbId())+"-checkIfEpisodeOnAir",false);
        return true;

    }



    private void updateSerieStatus2(User user, Long tvTmdbId) {
        Optional<UsersWatchedSeries> optionalUserWatchedSeries = this.usersWatchedSeriesRepository.findByImdbIdAndUserMail(tvTmdbId, user.getUsername());
        List<SerieHasSeason> allSeasons = this.serieHasSeasonRepository.findAllRelatedSeason(tvTmdbId);
        Optional<Serie> serie = this.tvRepository.findByTmdbId(tvTmdbId);
        long nbSeasonsSeen = getNbSeasonsWatchedForSerie(tvTmdbId, user);

        if (nbSeasonsSeen > 0) {
            if (optionalUserWatchedSeries.isPresent()) {
                optionalUserWatchedSeries.get().setStatus(Status.WATCHING);
                this.usersWatchedSeriesRepository.save(optionalUserWatchedSeries.get());
            } else {
                UsersWatchedSeries newWatchedSeries = new UsersWatchedSeries();
                newWatchedSeries.setSerie(serie.orElseThrow(() -> new IllegalStateException("Serie not found")));
                newWatchedSeries.setUser(user);
                newWatchedSeries.setStatus(Status.WATCHING);
                this.usersWatchedSeriesRepository.save(newWatchedSeries);
            }
        }
        if (nbSeasonsSeen == allSeasons.size()) {
            if (optionalUserWatchedSeries.isPresent()) {
                optionalUserWatchedSeries.get().setStatus(Status.SEEN);
                this.usersWatchedSeriesRepository.save(optionalUserWatchedSeries.get());
            } else {
                UsersWatchedSeries newWatchedSeries = new UsersWatchedSeries();
                newWatchedSeries.setSerie(serie.orElseThrow(() -> new IllegalStateException("Serie not found")));
                newWatchedSeries.setUser(user);
                newWatchedSeries.setStatus(Status.SEEN);
                this.usersWatchedSeriesRepository.save(newWatchedSeries);
            }
        }

    }

    private Status updateSeasonStatus2(Long tvSeasonTmdbId, String username, Long tvTmdbId) {
        Optional<User> optionalUser = userRepository.findUserByEmail(username);
        User user = optionalUser.orElseThrow(() -> new IllegalStateException(UserNotFound));
        Long userId = user.getId();

        List<SeasonHasEpisode> allEpisodes = this.seasonHasEpisodeRepository.findBySeasonImdbId(tvSeasonTmdbId);
        long nbEpisodesSeen = getNbEpisodesWatchedForSeason(tvSeasonTmdbId, username);

        Status returnedStatus = Status.NOTSEEN;
        // Si la relation n'existe pas, on la crée
        Optional<UsersWatchedSeason> optionalUserWatchedSeason = this.usersWatchedSeasonRepository.findByTmdbIdAndUserId(tvSeasonTmdbId, userId);


        // update la relation si elle existe au dessus sinon la récup puis l'update selon condition
        if (nbEpisodesSeen == allEpisodes.size()) {
            if (optionalUserWatchedSeason.isPresent()) {
                optionalUserWatchedSeason.get().setStatus(Status.SEEN);
                returnedStatus = Status.SEEN;
                this.usersWatchedSeasonRepository.save(optionalUserWatchedSeason.get());
                updateSerieStatus2(user, tvTmdbId);

            } else {
                Optional<UsersWatchedSeason> userWatchedSeason = this.usersWatchedSeasonRepository.findByTmdbIdAndUserId(tvSeasonTmdbId, userId);
                if (userWatchedSeason.isPresent()) {
                    userWatchedSeason.get().setStatus(Status.SEEN);
                    returnedStatus = Status.SEEN;
                    this.usersWatchedSeasonRepository.save(userWatchedSeason.get());
                    updateSerieStatus2(user, tvTmdbId);
                }
            }
        } else if (nbEpisodesSeen > 0) {
            if (optionalUserWatchedSeason.isPresent()) {
                optionalUserWatchedSeason.get().setStatus(Status.WATCHING);
                returnedStatus = Status.WATCHING;
                this.usersWatchedSeasonRepository.save(optionalUserWatchedSeason.get());
                updateSerieStatus(user, tvTmdbId);

            } else {
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

    public User addSeasonInWatchlistAsync(UserWatchedTvSeasonAddV2Dto userWatchedTvSeasonAddDto,User user) throws URISyntaxException, IOException, InterruptedException {
        //create serie or get it

        Serie serie = this.tvService.getSerieOrCreateIfNotExist(userWatchedTvSeasonAddDto.getTvTmdbId());
        Status returnedStatus = Status.NOTSEEN;

        //remove to watchlist bcz seen
        if (user.getWatchlistSeries().contains(serie)) {
            user.getWatchlistSeries().remove(serie);
            userRepository.save(user);

        }



        // add season to user season's count
        if (!user.getWatchedSeasons().contains(userWatchedTvSeasonAddDto.getSeason())) {
            user.getWatchedSeasons().remove(userWatchedTvSeasonAddDto.getSeason());
            userRepository.save(user);
            user.getWatchedSeasons().add(userWatchedTvSeasonAddDto.getSeason());
            userRepository.save(user);
        }

        // seek new relation user_season and update status
        Optional<UsersWatchedSeason> relationUserSeasonWhenCreated = usersWatchedSeasonRepository.findByTmdbIdAndUserId(
                userWatchedTvSeasonAddDto.getSeason().getTmdbSeasonId(),
                user.getId()
        );

        if (relationUserSeasonWhenCreated.isPresent()) {
            returnedStatus = Status.SEEN;
            relationUserSeasonWhenCreated.get().setStatus(Status.SEEN);
            usersWatchedSeasonRepository.save(relationUserSeasonWhenCreated.get());
            updateSerieStatus(user, serie.getTmdbId());
        }
        // create relation user_episode for every ep of the season
        createOrIncreaseUserWatchedEpisodeRelationAsync(userWatchedTvSeasonAddDto, user);

        return user;
    }

    public void createOrIncreaseUserWatchedEpisodeRelationAsync(UserWatchedTvSeasonAddV2Dto userWatchedTvSeasonAddDto, User user) {
        List<SeasonHasEpisode> seasonHasEpisodes = seasonHasEpisodeRepository.findBySeasonImdbId(userWatchedTvSeasonAddDto.getSeason().getTmdbSeasonId());

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<CompletableFuture<Void>> futures = seasonHasEpisodes.stream()
                .parallel()
                .map(seasonHasEpisode -> CompletableFuture.runAsync(() -> {
                    Episode episode = seasonHasEpisode.getEpisode();
                    if (!user.getWatchedEpisodes().contains(episode)) {
                        increaseDurationSerieDto increaseDurationSerieDto = new increaseDurationSerieDto();
                        increaseDurationSerieDto.setUsername(userWatchedTvSeasonAddDto.getUserMail());
                        increaseDurationSerieDto.setSeasonNumber(seasonHasEpisode.getSeason().getSeason_number());
                        increaseDurationSerieDto.setEpisodeNumber(episode.getEpisode_number());
                        increaseDurationSerieDto.setTvTmdbId(userWatchedTvSeasonAddDto.getTvTmdbId());
                        try {
                            if (checkIfEpisodeOnAir(increaseDurationSerieDto)) {
                                user.getWatchedEpisodes().remove(episode);
                                userRepository.save(user);
                                user.getWatchedEpisodes().add(episode);
                                increaseWatchedDurationSeries(increaseDurationSerieDto);
                                userRepository.save(user);
                            }
                        } catch (URISyntaxException e) {
                            user.getWatchedEpisodes().remove(episode);
                            userRepository.save(user);
                        } catch (IOException e) {
                            user.getWatchedEpisodes().remove(episode);
                            userRepository.save(user);
                        } catch (InterruptedException e) {
                            user.getWatchedEpisodes().remove(episode);
                            userRepository.save(user);
                        }catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                    } else {
                        // increase watch_number
                        Optional<UsersWatchedEpisode> usersWatchedEpisode = usersWatchedEpisodeRepository.findByEpisodeImdbIdAndUserId(episode.getImbd_id(), user.getId());
                        usersWatchedEpisode.get().setWatchedNumber(usersWatchedEpisode.get().getWatchedNumber() + 1L);
                        usersWatchedEpisodeRepository.save(usersWatchedEpisode.get());
                    }
                }, executor))
                .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        allFutures.join();

        executor.shutdown();
    }


    public boolean checkIfEpisodeOnAirAsync(increaseDurationSerieDto increaseDurationSerieDto) throws URISyntaxException, IOException, InterruptedException {
        String urlToCall = "https://api.themoviedb.org/3/tv/" + increaseDurationSerieDto.getTvTmdbId() + "/season/" + increaseDurationSerieDto.getSeasonNumber() + "/episode/" + increaseDurationSerieDto.getEpisodeNumber() + "?api_key=" + this.apiKey;
        JSONObject checkInCache = this.redisService.getDataFromRedisForInternalRequest(urlToCall);
        Gson gson = new Gson();
        AddSeasonDto result_search = gson.fromJson(String.valueOf(checkInCache), AddSeasonDto.class);
        LocalDate today = LocalDate.now();
        LocalDate dateOnAir = null;
        try {
            if (result_search.air_date != null) {
                dateOnAir = LocalDate.parse(result_search.air_date);
                return dateOnAir.isBefore(today) || dateOnAir.isEqual(today);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        return false;

    }
}