package com.m2i.showtime.yak.common.trophy.list;

import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.Trophy;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.TrophyRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import com.m2i.showtime.yak.Service.KafkaMessageGeneratorService;
import com.m2i.showtime.yak.Service.MovieService;
import com.m2i.showtime.yak.common.trophy.TrophyInterface;
import com.m2i.showtime.yak.common.trophy.TrophyType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component

public class MovieWatcherPlatineTrophy implements TrophyInterface {
    private final MovieService movieService;
    private final TrophyRepository trophyRepository;
    private final UserRepository userRepository;
    private final String name = "Movie Watcher";
    private final String description = "Watch 100 movies";
    private final String image = "";
    private final TrophyType type = TrophyType.PLATINUM;

    private final KafkaMessageGeneratorService kafkaMessageGeneratorService;
    private final MovieRepository movieRepository;

    public MovieWatcherPlatineTrophy(MovieService movieService, TrophyRepository trophyRepository, UserRepository userRepository, KafkaMessageGeneratorService kafkaMessageGeneratorService,
                                     MovieRepository movieRepository) {
        this.movieService = movieService;
        this.trophyRepository = trophyRepository;
        this.userRepository = userRepository;
        this.kafkaMessageGeneratorService = kafkaMessageGeneratorService;
        this.movieRepository = movieRepository;
    }
    @Override
    public void createTrophyIfNotExist() {
        trophyRepository.findByNameAndType(this.name,this.type).orElseGet(() -> {
            return trophyRepository.save(new Trophy(this.name, this.description, this.image, this.type));
        });
    }

    @Override
    public void updateTrophyIfNecessary() {
        Optional<Trophy> trophyOptional = trophyRepository.findByNameAndType(this.name, this.type);
        Trophy trophy = new Trophy(this.name, this.description, this.image, this.type);
        trophy.setId(trophyOptional.get().getId());

        ModelMapper modelMapper = new ModelMapper();
        if (trophyOptional.isPresent() && !trophyOptional.get().equals(trophy)) {
            Trophy trophy1 = trophyOptional.get();
            trophy1 = modelMapper.map(trophy, Trophy.class);
            trophyRepository.save(trophy1);
        }

    }

    @Override
    public void checkTrophy(String username, long elementId) {
        createTrophyIfNotExist();
        updateTrophyIfNecessary();
        Optional<Trophy> trophy = trophyRepository.findByNameAndType(this.name, this.type);
         User user = userRepository.findUserByEmail(username).orElseThrow(() -> {
            return new RuntimeException("User not found");
        });
         Optional<Movie> movie = movieRepository.findById(elementId);
         if(!movie.isPresent()){
             return ;
         }
        if (user.getWatchedMovies().size() >= 100 && !user.getTrophy().contains(trophy.get())) {
            user.getTrophy().add(trophy.get());
            userRepository.save(user);
            NotifyUser(username, elementId);
        } else if (user.getWatchedMovies().size() < 100 && user.getTrophy().contains(trophy.get())) {
            user.getTrophy().remove(trophy.get());
            userRepository.save(user);
        }
    }

    @Override
    public void NotifyUser(String username, long elementId) {
        kafkaMessageGeneratorService.sendTrophyMessage(username, this.name, this.image, this.type);
    }

    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public String getDescription() {
        return this.description;
    }
    @Override
    public String getImage() {
        return this.image;
    }
    @Override
    public TrophyType getTrophyType() {
        return this.type;
    }

}
