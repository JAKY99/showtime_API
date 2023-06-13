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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

import static com.m2i.showtime.yak.Security.Role.AppUserRole.*;
import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@SpringBootApplication
@ConfigurationPropertiesScan
public class ShowTimeApplication {
	private static final Logger LOGGER = LogManager.getLogger(ShowTimeApplication.class);
	private final PasswordEncoder passwordEncoder;

	public ShowTimeApplication(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public static void main(String[] args) {
		SpringApplication.run(ShowTimeApplication.class, args);
	}
	@Bean
	public Docket springfoxAppInfo() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("Springfox-api")
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(regex("/api/v1/.*"))
				.build()
				.apiInfo(new ApiInfo(
						"Showtime API",
						"Route map",
						"",
						"",
						null,
						"License of API",
						"API license URL",
						Collections.emptyList()));
	}
}
