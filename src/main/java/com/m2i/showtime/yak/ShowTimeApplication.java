package com.m2i.showtime.yak;

import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Entity.Permission;
import com.m2i.showtime.yak.Entity.Role;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.RoleRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import com.m2i.showtime.yak.Service.User.UserAuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Set;

import static com.m2i.showtime.yak.Security.Role.AppUserRole.*;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ShowTimeApplication {

	private final PasswordEncoder passwordEncoder;

	public ShowTimeApplication(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public static void main(String[] args) {
		SpringApplication.run(ShowTimeApplication.class, args);
	}
		@Bean
		public CommandLineRunner mappingDemo(UserRepository userRepo,
											 MovieRepository movieRepo,
											 RoleRepository roleRepo,
											 UserAuthService userAuthService) {
			return args -> {

				Permission permission1 = new Permission("user:read");
				Permission permission2 = new Permission("user:delete");
				Permission permission3 = new Permission("user:edit");
				Permission permission4 = new Permission("user:manage_users");
				Permission permission5 = new Permission("user:manage_rank");
				Permission permission6 = new Permission("user:manage_trophy");
				Permission permission7 = new Permission("user:manage_permission");
				Permission permission8 = new Permission("user:manage_watched");
				Permission permission9 = new Permission("movie:read");
				Permission permission10 = new Permission("movie:manage");

				Role role_admin = new Role(
						"ADMIN",
						"Administrator",
						"",
						Set.of(
								permission4,
								permission5,
								permission6,
								permission7,
								permission8,
								permission10
						)
				);
				Role role_user = new Role(
						"USER",
						"User",
						"",
						Set.of(
								permission1,
								permission2,
								permission3,
								permission9
						)
				);
				roleRepo.saveAll(Arrays.asList(role_admin, role_user));

				User user_admin = new User(
						"Achot-ADMIN",
						"Barseghyan",
						"achot.barseghyan@gmail.com",
						passwordEncoder.encode("123"),
						"France",
						role_admin
				);
				User user_user = new User(
						"Achot-USER",
						"Barseghyan",
						"user.barseghyan@gmail.com",
						passwordEncoder.encode("123"),
						"France",
						role_user
				);
				User user_register = new User(
						"Achot-REGISTER",
						"Barseghyan",
						"register.barseghyan@gmail.com",
						passwordEncoder.encode("123"),
						"France"
				);
				userRepo.save(user_admin);
				userRepo.save(user_user);
				userAuthService.register(user_register);

				Movie movie1 = new Movie(1L,"Matrix");
				Movie movie2 = new Movie(2L,"Avatar");
				Movie movie3 = new Movie(3L,"Inception");

				movieRepo.saveAll(Arrays.asList(movie1, movie2, movie3));

				user_user.getWatchedMovies().addAll(Arrays.asList(movie1, movie2, movie3));

				userRepo.save(user_user);
			};
		}

}