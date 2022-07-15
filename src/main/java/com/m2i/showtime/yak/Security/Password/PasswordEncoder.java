package com.m2i.showtime.yak.Security.Password;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class PasswordEncoder {
    @Bean
    public static String encodePassword(String password){

        int saltLength = 16; // salt length in bytes
        int hashLength = 32; // hash length in bytes
        int parallelism = 1; // currently not supported by Spring Security
        int memory = 4096;   // memory costs
        int iterations = 3;

        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(
                saltLength,
                hashLength,
                parallelism,
                memory,
                iterations);

        return argon2PasswordEncoder.encode(password);
    }
}
