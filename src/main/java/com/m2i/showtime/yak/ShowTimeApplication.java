package com.m2i.showtime.yak;

import com.m2i.showtime.yak.movie.Movie;
import com.m2i.showtime.yak.movie.MovieRepository;
import com.m2i.showtime.yak.user.User;
import com.m2i.showtime.yak.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class ShowTimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShowTimeApplication.class, args);

	}
		@Bean
		public CommandLineRunner mappingDemo(UserRepository userRepo,
				MovieRepository movieRepo) {
			return args -> {

				User user = new User(
						"Achot",
						"Barseghyan",
						"achot.barseghyan@gmail.com",
						"password",
						"France"
				);

				userRepo.save(user);

				Movie course1 = new Movie(1L,"Matrix");
				Movie course2 = new Movie(2L,"Avatar");
				Movie course3 = new Movie(3L,"Inception");

				movieRepo.saveAll(Arrays.asList(course1, course2, course3));

				user.getWatchedMovies().addAll(Arrays.asList(course1, course2, course3));

				userRepo.save(user);
			};
		}

}
