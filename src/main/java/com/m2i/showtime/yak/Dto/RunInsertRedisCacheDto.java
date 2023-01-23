package com.m2i.showtime.yak.Dto;

import com.m2i.showtime.yak.Configuration.RedisConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RunInsertRedisCacheDto {
    private  RedisConfig redisConfig;
    private  String urlApi;

    public RunInsertRedisCacheDto(RedisConfig redisConfig, String urlApi) {
        this.redisConfig = redisConfig;
        this.urlApi = urlApi;
    }
}
