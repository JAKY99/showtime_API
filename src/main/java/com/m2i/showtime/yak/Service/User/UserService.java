package com.m2i.showtime.yak.Service.User;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.m2i.showtime.yak.Dto.UserSimpleDto;
import com.m2i.showtime.yak.Dto.UserWatchedMovieAddDto;
import com.m2i.showtime.yak.Dto.UserWatchedMovieDto;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import com.m2i.showtime.yak.Service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieService movieService;
    @Value("${application.bucketName}")
    private String bucketName;
    @Value("${application.awsAccessKey}")
    private String awsAccessKey;
    @Value("${application.awsSecretKey}")
    private String awsSecretKey;

    @Autowired
    public UserService(UserRepository userRepository, MovieRepository movieRepository, MovieService movieService) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.movieService = movieService;
    }

    public Optional<UserSimpleDto> getUser(Long userId) {
        Optional<UserSimpleDto> user = userRepository.findSimpleUserById(userId);
        return user;
    }

    public Optional<UserSimpleDto> getUserByEmail(String email) {
        Optional<UserSimpleDto> user = userRepository.findSimpleUserByEmail(email);
        return user;
    }

    public void addUser(User user) {

        Optional<User> userOptional = userRepository.findUserByEmail(user.getUsername());

        if (userOptional.isPresent()) {
            throw new IllegalStateException("email taken");
        }

        userRepository.save(user);
    }

    public void deleteUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalStateException("User does not exists");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateUser(Long userId, User modifiedUser) {

        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalStateException(
                                          ("user with id " + userId + "does not exists")));

        if (modifiedUser.getFirstName() != null && modifiedUser.getFirstName()
                                                               .length() > 0 && !Objects.equals(
                user.getFirstName(), modifiedUser.getFirstName())) {
            user.setFirstName(modifiedUser.getFirstName());
        }

        if (modifiedUser.getLastName() != null && modifiedUser.getLastName()
                                                              .length() > 0 && !Objects.equals(
                user.getLastName(), modifiedUser.getLastName())) {
            user.setLastName(modifiedUser.getLastName());
        }

        if (modifiedUser.getCountry() != null && modifiedUser.getCountry()
                                                             .length() > 0 && !Objects.equals(
                user.getCountry(), modifiedUser.getCountry())) {
            user.setCountry(modifiedUser.getCountry());
        }

        if (modifiedUser.getUsername() != null && modifiedUser.getUsername()
                                                              .length() > 0 && !Objects.equals(
                user.getUsername(), modifiedUser.getUsername())) {
            if (userRepository.findUserByEmail(modifiedUser.getUsername())
                              .isPresent()) {
                throw new IllegalStateException("email taken");
            }
            user.setUsername(modifiedUser.getUsername());
        }
    }

    public boolean isMovieInWatchlist(UserWatchedMovieDto userWatchedMovieDto) {
        Optional<UserSimpleDto> user = userRepository.isMovieWatched(
                userWatchedMovieDto.getUserMail(), userWatchedMovieDto.getMovieId());

        if (user.isEmpty()) {
            return false;
        } else {
            return true;
        }

    }


    public boolean addMovieInWatchlist(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        Movie movie = movieService.getMovieOrCreateIfNotExist(userWatchedMovieAddDto.getMovieId(),
                                                              userWatchedMovieAddDto.getMovieName());

        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });

        optionalUser.get()
                    .getWatchedMovies()
                    .add(movie);

        userRepository.saveAndFlush(user);

        return true;
    }


    public boolean removeMovieInWatchlist(UserWatchedMovieAddDto userWatchedMovieAddDto) {
        Movie movie = movieService.getMovieOrCreateIfNotExist(userWatchedMovieAddDto.getMovieId(),
                                                              userWatchedMovieAddDto.getMovieName());

        Optional<User> optionalUser = userRepository.findUserByEmail(userWatchedMovieAddDto.getUserMail());
        User user = optionalUser.orElseThrow(() -> {
            throw new IllegalStateException("User not found");
        });

        optionalUser.get()
                    .getWatchedMovies()
                    .remove(movie);

        userRepository.saveAndFlush(user);

        return true;
    }

    public boolean increaseWatchedNumber(UserWatchedMovieAddDto userWatchedMovieAddDto) {


        return true;
    }

    public String uploadProfilePic(Long userId, @RequestParam("file") MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalStateException(
                                          ("user with id " + userId + "does not exists")));
        String fileName = userId + "_profile_pic." + file.getOriginalFilename().split("\\.")[1];
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
        file.transferTo( new File(basePath + "/src/main/profile_pic_temp/"+fileName));
        File fileToUpload = new File( basePath + "/src/main/profile_pic_temp/"+fileName);
        s3client.putObject(
                this.bucketName,
                fileName,
                fileToUpload
        );
        fileToUpload.delete();
        return "done";
    }
}
