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
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.m2i.showtime.yak.Configuration.RedisConfig;
import com.m2i.showtime.yak.Configuration.RedisLetuceConfig;
import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Entity.UsersWatchedMovie;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import com.m2i.showtime.yak.Repository.UsersWatchedMovieRepository;
import com.m2i.showtime.yak.Service.MovieService;
import com.m2i.showtime.yak.Service.RedisService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import java.io.OutputStream;
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
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieService movieService;
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
    @Autowired
    public UserService(UserRepository userRepository, MovieRepository movieRepository, MovieService movieService, UsersWatchedMovieRepository usersWatchedMovieRepository, RedisService redisService) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.movieService = movieService;
        this.usersWatchedMovieRepository = usersWatchedMovieRepository;
        this.redisService = redisService;


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

        if (user.isEmpty()) {
            return false;
        } else {
            return true;
        }

    }


    public boolean addMovieInWatchlist(UserWatchedMovieAddDto userWatchedMovieAddDto) throws URISyntaxException, IOException, InterruptedException {
        Movie movie = movieService.getMovieOrCreateIfNotExist(userWatchedMovieAddDto.getTmdbId(),
                                                              userWatchedMovieAddDto.getMovieName());

        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
        Long movieId = movieRepository.findByTmdbId(userWatchedMovieAddDto.getTmdbId()).get().getId();
        Long userId = optionalUser.get().getId();
        Optional<UsersWatchedMovie> optionalUserWatchedMovie =  usersWatchedMovieRepository.findByMovieAndUserId(movieId,userId );
        if(!optionalUserWatchedMovie.isPresent()){
            optionalUser.get()
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


    public void removeMovieInWatchlist(UserWatchedMovieAddDto userWatchedMovieAddDto) throws URISyntaxException, IOException, InterruptedException {
        Movie movie = movieService.getMovieOrCreateIfNotExist(userWatchedMovieAddDto.getTmdbId(),
                                                              userWatchedMovieAddDto.getMovieName());

        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
        Long movieId = movieRepository.findByTmdbId(userWatchedMovieAddDto.getTmdbId()).get().getId();
        Long userId = optionalUser.get().getId();
        Optional<UsersWatchedMovie> completeUserWatched =  usersWatchedMovieRepository.findByMovieAndUserId(movieId,userId );
//                .orElseThrow(() -> {
//                    throw new IllegalStateException("User not found");
//                });
        multiplicatorTime = completeUserWatched.get().getWatchedNumber().intValue();
//
        this.decreaseWatchedNumber(userWatchedMovieAddDto);
        this.decreaseTotalMovieWatchedTime(userWatchedMovieAddDto);
        optionalUser.get()
                    .getWatchedMovies()
                    .remove(movie);

        userRepository.save(user);
    }

    public void increaseWatchedNumber(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });

         user.setTotalMovieWatchedNumber(optionalUser.get().getTotalMovieWatchedNumber() + 1);
         userRepository.save(optionalUser.get());
    }
    public void decreaseWatchedNumber(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
        Long movieId = movieRepository.findByTmdbId(userWatchedMovieAddDto.getTmdbId()).get().getId();
        Long userId = optionalUser.get().getId();
        UsersWatchedMovie completeUserWatched =  usersWatchedMovieRepository.findByMovieAndUserId(movieId,userId )
                .orElseThrow(() -> {
                    throw new IllegalStateException("User not found");
                });
        optionalUser.get().setTotalMovieWatchedNumber(optionalUser.get().getTotalMovieWatchedNumber() - completeUserWatched.getWatchedNumber());


        userRepository.save(optionalUser.get());

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
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
        Long newWatchedTotalTime = optionalUser.get().getTotalMovieWatchedTime().getSeconds()+Duration.ofSeconds(result_search.getRuntime()*60).getSeconds();
        optionalUser.get().setTotalMovieWatchedTime(Duration.ofSeconds(newWatchedTotalTime));

        userRepository.save(optionalUser.get());
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
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
        Long newWatchedTotalTime = optionalUser.get().getTotalMovieWatchedTime().getSeconds()-Duration.ofSeconds(result_search.getRuntime()*60).getSeconds()*multiplicatorTime;

        optionalUser.get().setTotalMovieWatchedTime(Duration.ofSeconds(newWatchedTotalTime));

        userRepository.save(optionalUser.get());
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
        file.transferTo( new File(basePath + "/src/main/profile_pic_temp/original_"+fileName));
        File originalFile = new File(basePath + "/src/main/profile_pic_temp/original_"+fileName);
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
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });

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
            DecodedJWT jwt = verifier.verify(token);
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
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
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
        String checkInCache = this.redisService.getRedisCacheDataBDD(inCacheKey);
        if(checkInCache!=null){
            return new ObjectMapper().readValue(checkInCache, ProfileLazyUserDtoHeader.class);
        }
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
        ProfileLazyUserDtoHeader profileLazyUserDtoHeader = new ProfileLazyUserDtoHeader();
        profileLazyUserDtoHeader.setNumberOfWatchedSeries(optionalUser.get().getTotalSeriesWatchedNumber());
        profileLazyUserDtoHeader.setNumberOfWatchedMovies(optionalUser.get().getTotalMovieWatchedNumber());
        String totalDurationMoviesMonthDayHour = durationConvertor(optionalUser.get().getTotalMovieWatchedTime());
        profileLazyUserDtoHeader.setTotalTimeWatchedMovies(totalDurationMoviesMonthDayHour);
        String totalDurationSeriesMonthDayHour = durationConvertor(optionalUser.get().getTotalSeriesWatchedTime());
        profileLazyUserDtoHeader.setTotalTimeWatchedSeries(totalDurationSeriesMonthDayHour);
        this.redisService.setRedisCacheDataBDD(inCacheKey, new ObjectMapper().writeValueAsString(profileLazyUserDtoHeader),60);
        return profileLazyUserDtoHeader;
    }

    public String durationConvertor(Duration duration){
        duration = Duration.ofDays(duration.toDaysPart()).plusHours(duration.toHoursPart());

        Period period = Period.between(LocalDate.ofEpochDay(0), LocalDate.ofEpochDay(duration.toDays()));
        int months = period.getMonths();
        int days = period.getDays();
        int hours = (int) (duration.toHours() - (days + months * 30) * 24);
        String formattedDuration = months + "/" + days + "/" + hours;
        return formattedDuration;
    }

    public ProfileLazyUserDtoLastWatchedMovies getProfileLastWatchedMoviesData(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
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
        profileLazyUserDtoLastWatchedMovies.setLastWatchedMovies(Arrays.stream(lastWatchedMoviesIds.get()).limit(10).toArray());
        profileLazyUserDtoLastWatchedMovies.setFavoritesMovies(favoriteMoviesIds);
        profileLazyUserDtoLastWatchedMovies.setWatchlistMovies(watchlistMoviesIds);
        profileLazyUserDtoLastWatchedMovies.setTotalFavoritesMovies(user.getFavoriteMovies().size());
        profileLazyUserDtoLastWatchedMovies.setTotalWatchedMovies(lastWatchedMoviesIds.get().length);
        profileLazyUserDtoLastWatchedMovies.setTotalWatchlistMovies(user.getWatchlistMovies().size());
        return profileLazyUserDtoLastWatchedMovies;
    }



    public ProfileLazyUserDtoSocialInfos getProfileSocialInfos(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
        ProfileLazyUserDtoSocialInfos profileLazyUserDtoSocialInfos = new ProfileLazyUserDtoSocialInfos();
        profileLazyUserDtoSocialInfos.setFollowersCounter(user.getFollowersCounter());
        profileLazyUserDtoSocialInfos.setFollowingsCounter(user.getFollowingsCounter());
        profileLazyUserDtoSocialInfos.setCommentsCounter(user.getCommentsCounter());
        return profileLazyUserDtoSocialInfos;


    }

    public ProfileLazyUserDtoAvatar getProfileAvatar(String email) {
        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
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
        file.transferTo( new File(basePath + "/src/main/profile_pic_temp/original_"+fileName));
        File originalFile = new File(basePath + "/src/main/profile_pic_temp/original_"+fileName);

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
        fileToUpload.delete();
        originalFile.delete();
        UploadBackgroundDtoResponse uploadBackgroundDtoResponse = new UploadBackgroundDtoResponse();
        uploadBackgroundDtoResponse.setNewBackgroundUrl(url);
        return uploadBackgroundDtoResponse;
    }

    public boolean toggleMovieInFavoritelist(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        Movie movie = movieService.getMovieOrCreateIfNotExist(userWatchedMovieAddDto.getTmdbId(),
                userWatchedMovieAddDto.getMovieName());

        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
        if(!optionalUser.get().getFavoriteMovies().contains(movie)){
            optionalUser.get()
                    .getFavoriteMovies()
                    .add(movie);
            userRepository.save(optionalUser.get());
            return true;
        }
        if(optionalUser.get().getFavoriteMovies().contains(movie)){
            optionalUser.get()
                    .getFavoriteMovies()
                    .remove(movie);
            userRepository.save(optionalUser.get());
            return false;
        }
        return false;
    }
    public boolean toggleMovieInMovieToWatchlist(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        Movie movie = movieService.getMovieOrCreateIfNotExist(userWatchedMovieAddDto.getTmdbId(),
                userWatchedMovieAddDto.getMovieName());

        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
        if(!optionalUser.get().getWatchlistMovies().contains(movie)){
            optionalUser.get()
                    .getWatchlistMovies()
                    .add(movie);
            userRepository.save(optionalUser.get());
            return true;
        }
        if(optionalUser.get().getWatchlistMovies().contains(movie)){
            optionalUser.get()
                    .getWatchlistMovies()
                    .remove(movie);
            userRepository.save(optionalUser.get());
            return false;
        }
        return false;
    }

    public boolean isMovieInMovieToWatchlist(UserWatchedMovieAddDto userWatchedMovieDto) {
        Optional<UserSimpleDto> user = userRepository.isMovieInMovieToWatch(
                userWatchedMovieDto.getUserMail(), userWatchedMovieDto.getTmdbId());

        if (user.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isMovieInFavoritelist(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        Optional<UserSimpleDto> user = userRepository.isMovieInFavorite(
                userWatchedMovieAddDto.getUserMail(), userWatchedMovieAddDto.getTmdbId());

        if (user.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public fetchRangeListDto lastWatchedMoviesRange(fetchRangeDto fetchRangeDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(fetchRangeDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
        Optional<long[]> lastWatchedMoviesIds = usersWatchedMovieRepository.findWatchedMoviesByUserId(user.getId());
        long[] listToUse = Arrays.stream(lastWatchedMoviesIds.get())
                .skip(fetchRangeDto.getCurrentLength())
                .limit(fetchRangeDto.getCurrentLength()+10)
        .toArray();
        fetchRangeListDto fetchRangeListDto = new fetchRangeListDto();
        fetchRangeListDto.setTmdbIdList(listToUse);
        return fetchRangeListDto;
    }
    public fetchRangeListDto favoritesMoviesRange(fetchRangeDto fetchRangeDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(fetchRangeDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
        Movie[] listRange = Arrays.stream(user.getFavoriteMovies().toArray())
                .skip(fetchRangeDto.getCurrentLength())
                .limit(fetchRangeDto.getCurrentLength()+10)
                .toArray(Movie[]::new);
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
    public fetchRangeListDto watchlistMoviesRange(fetchRangeDto fetchRangeDto) {
        Optional<User> optionalUser = userRepository.findUserByEmail(fetchRangeDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });
        Movie[] listRange = Arrays.stream(user.getWatchlistMovies().toArray())
                .skip(fetchRangeDto.getCurrentLength())
                .limit(fetchRangeDto.getCurrentLength()+10)
                .toArray(Movie[]::new);
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
        // g.setColor(new Color(color, false));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return target;
    }
    public BufferedImage createImage(int width, int height, boolean hasAlpha) {
        return new BufferedImage(width, height, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
    }
}