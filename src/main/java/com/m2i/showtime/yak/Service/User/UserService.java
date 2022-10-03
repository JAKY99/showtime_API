package com.m2i.showtime.yak.Service.User;

import com.m2i.showtime.yak.Dto.UserSimpleDto;
import com.m2i.showtime.yak.Dto.UserWatchedMovieAddDto;
import com.m2i.showtime.yak.Dto.UserWatchedMovieDto;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.apache.catalina.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    @Autowired
    public UserService(UserRepository userRepository,MovieRepository movieRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
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

        if (userOptional.isPresent()){
            throw new IllegalStateException("email taken");
        }

        userRepository.save(user);
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
        Optional<User> user = userRepository.findUserByEmail(userWatchedMovieDto.getUserMail());

        Long check;
        check = user.get().getWatchedMovies().stream().filter(x->x.getId()==userWatchedMovieDto.getMovieId()).count();
        if(check==1){
            return true;
        }
        return false;

    }
    public boolean addMovieInWatchlist(UserWatchedMovieAddDto UserWatchedMovieAddDto) {
        boolean movie = movieRepository.findById(UserWatchedMovieAddDto.getMovieId()).isPresent();

        if(!movie){

            Movie movie1 = new Movie(UserWatchedMovieAddDto.getMovieId(),UserWatchedMovieAddDto.getMovieName());
            movieRepository.saveAll(Arrays.asList(movie1));

        }
        Optional<Movie>  movie1 = movieRepository.findById(UserWatchedMovieAddDto.getMovieId());
        Optional<User> user = userRepository.findUserByEmail(UserWatchedMovieAddDto.getUserMail());
        user.get().getWatchedMovies().add(movie1.get());

        userRepository.saveAndFlush(user.get());

        return true;

    }
    public boolean removeMovieInWatchlist(UserWatchedMovieAddDto UserWatchedMovieAddDto) {
        boolean movie = movieRepository.findById(UserWatchedMovieAddDto.getMovieId()).isPresent();

        if(!movie){

            Movie movie1 = new Movie(UserWatchedMovieAddDto.getMovieId(),UserWatchedMovieAddDto.getMovieName());
            movieRepository.saveAll(Arrays.asList(movie1));

        }
        Optional<Movie>  movie1 = movieRepository.findById(UserWatchedMovieAddDto.getMovieId());
        Optional<User> user = userRepository.findUserByEmail(UserWatchedMovieAddDto.getUserMail());
        user.get().getWatchedMovies().remove(movie1.get());

        userRepository.saveAndFlush(user.get());

        return true;

    }
}
