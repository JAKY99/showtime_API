package com.m2i.showtime.yak.common.trophy.list;

import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.Trophy;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.TrophyRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import com.m2i.showtime.yak.Service.KafkaMessageGeneratorService;
import com.m2i.showtime.yak.Service.MovieService;
import com.m2i.showtime.yak.common.trophy.TrophyActionName;
import com.m2i.showtime.yak.common.trophy.TrophyInterface;
import com.m2i.showtime.yak.common.trophy.TrophyType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component

public class MovieWatcherPlatineTrophy implements TrophyInterface {
    private final TrophyRepository trophyRepository;
    private final UserRepository userRepository;
    private final String name = "Movie Watcher";
    private final String description = "Watch 100 movies";
    private final String image = "";
    private final TrophyType type = TrophyType.PLATINUM;

    private final KafkaMessageGeneratorService kafkaMessageGeneratorService;

    public MovieWatcherPlatineTrophy(TrophyRepository trophyRepository,
                                     UserRepository userRepository,
                                     KafkaMessageGeneratorService kafkaMessageGeneratorService
                                     ) {
        this.trophyRepository = trophyRepository;
        this.userRepository = userRepository;
        this.kafkaMessageGeneratorService = kafkaMessageGeneratorService;
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
    @Transactional
    public void checkTrophy(String username, long elementId, TrophyActionName trophyActionName) {
        createTrophyIfNotExist();
        updateTrophyIfNecessary();
        Optional<Trophy> trophy = trophyRepository.findByNameAndType(this.name, this.type);
         User user = userRepository.findUserByEmail(username).orElseThrow(() -> {
            return new RuntimeException("User not found");
        });
        if(
                !trophyActionName.equals(TrophyActionName.ADD_MOVIE_IN_WATCHED_LIST)
                        && !trophyActionName.equals(TrophyActionName.REMOVE_MOVIE_IN_WATCHED_LIST)
        ){
            return ;
        }
        if (user.getWatchedMovies().size() >= 100 && !user.getTrophy().contains(trophy.get())) {
            user.getTrophy().add(trophy.get());
            userRepository.save(user);
            NotifyUser(username, elementId);
            NotifyFollowers(username, elementId);
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
    public void NotifyFollowers(String username, long elementId) {
        User user = userRepository.findUserByEmail(username).orElseThrow(() -> {
            return new RuntimeException("User not found");
        });
        user.getFollowers().forEach(follower -> {
            kafkaMessageGeneratorService.sendTrophyMessageToFollowers(follower.getUsername(),username, this.name, this.image, this.type);
        });
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
