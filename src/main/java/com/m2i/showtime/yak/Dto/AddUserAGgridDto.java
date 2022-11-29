package com.m2i.showtime.yak.Dto;

import com.m2i.showtime.yak.Entity.Role;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
@Getter
public class AddUserAGgridDto {
    private String username;
    private String firstName;
    private String password;
    private String lastName;
    private String country;
    private Role role;
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    public AddUserAGgridDto() {
    }

    public AddUserAGgridDto(String firstName, String lastName, String country, Role role, String password , String username) {
        PasswordEncoder passwordEncoder = this.encoder();
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.role = role;
        this.password = passwordEncoder.encode(password);
    }



}
