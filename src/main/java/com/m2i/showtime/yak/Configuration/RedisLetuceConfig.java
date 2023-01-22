package com.m2i.showtime.yak.Configuration;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisLetuceConfig {
    @Value("${application.redis.host}")
    private String redisHost;
    @Value("${application.redis.port}")
    private int redisPort;
    @Value("${application.redis.password}")
    private String redisPassword;

    @Bean
    public RedisClient redisClient() {
        RedisURI redisURI = RedisURI.builder()
                .withHost(redisHost)
                .withPort(redisPort)
                .withPassword(redisPassword)
                .build();
        return RedisClient.create(redisURI);
    }

}

