package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Configuration.RedisConfig;
import com.m2i.showtime.yak.Dto.RunInsertRedisCacheDto;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisConfig redisConfig;

    public RedisService(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    public String getRedisCache(String urlApi) {
        boolean check = redisConfig.jedis().get(urlApi)!=null;
        if(check) {
            return redisConfig.jedis().get(urlApi);
        }
        RunInsertRedisCacheDto runInfo = new RunInsertRedisCacheDto(redisConfig,urlApi);
        CustomThreadService thread = new CustomThreadService(runInfo,"insertUrlApiInRedis");
        thread.start();
        return urlApi;
    }
}
