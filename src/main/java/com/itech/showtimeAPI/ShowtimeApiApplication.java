package com.itech.showtimeAPI;

import com.itech.showtimeAPI.admin.Admin;
import com.itech.showtimeAPI.repository.AdminRepository;
import com.itech.showtimeAPI.repository.UserRepository;
import com.itech.showtimeAPI.user.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class ShowtimeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShowtimeApiApplication.class, args);
	}


}
