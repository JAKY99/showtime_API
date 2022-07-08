package com.m2i.showtime.yak.user;

import com.m2i.showtime.yak.movie.Movie;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

@Configuration
public class UserConfig {

/*    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository) {
        return args -> {
            User achot = new User(
                    "Achot",
                    "Barseghyan",
                    "achot.barseghyan@gmail.com",
                    "Password123",
                    "France",
                    Collection("The batman"));
            );

            userRepository.saveAll(
                    List.of(achot)
            );
        };
    }*/
}
