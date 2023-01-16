package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Configuration.RedisConfig;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisConfig redisConfig;

    public RedisService(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    public String getRedisCache(String urlApi) {

        return urlApi;
    }
}
