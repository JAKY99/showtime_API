package com.itech.showtimeAPI.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

import com.itech.showtimeAPI.user.User;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findUserByEmail(String email);
    
}
