package com.itech.showtimeAPI.repository;

import com.itech.showtimeAPI.consommer.Consumer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConsumerRepository extends MongoRepository<Consumer, String> {

    Optional<Consumer> findConsumerByEmail(String email);
    
}
