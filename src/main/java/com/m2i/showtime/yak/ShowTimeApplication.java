package com.m2i.showtime.yak;

import com.m2i.showtime.yak.user.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class ShowTimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShowTimeApplication.class, args);
	}

}
