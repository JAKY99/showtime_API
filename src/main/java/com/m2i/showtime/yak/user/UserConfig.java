package com.m2i.showtime.yak.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository) {
        return args -> {
            User achot = new User(
                    "Achot",
                    "Barseghyan",
                    "achot.barseghyan@gmail.com",
                    "Password123",
                    "France"
            );

            User kevin = new User(
                    "Kevin",
                    "Pognon",
                    "kevin.pognon@gmail.com",
                    "Password123",
                    "France"
            );

            userRepository.saveAll(
                    List.of(achot, kevin)
            );
        };
    }
}
