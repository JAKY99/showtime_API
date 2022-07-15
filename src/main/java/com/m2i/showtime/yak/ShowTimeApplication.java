package com.m2i.showtime.yak;

import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.Role;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.RoleRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class ShowTimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShowTimeApplication.class, args);

	}
		@Bean
		public CommandLineRunner mappingDemo(UserRepository userRepo,
											 MovieRepository movieRepo,
											 RoleRepository roleRepo) {
			return args -> {

				User user = new User(
						"Achot",
						"Barseghyan",
						"achot.barseghyan@gmail.com",
						"password",
						"France"
				);

				userRepo.save(user);

				Movie movie1 = new Movie(1L,"Matrix");
				Movie movie2 = new Movie(2L,"Avatar");
				Movie movie3 = new Movie(3L,"Inception");

				movieRepo.saveAll(Arrays.asList(movie1, movie2, movie3));

				user.getWatchedMovies().addAll(Arrays.asList(movie1, movie2, movie3));

				Role role = new Role("ADMIN");
				roleRepo.saveAll(Arrays.asList(role));

				user.getRoles().addAll(Arrays.asList(role));


				userRepo.save(user);
			};
		}

}
