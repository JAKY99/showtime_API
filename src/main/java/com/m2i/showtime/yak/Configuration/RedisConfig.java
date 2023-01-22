package com.m2i.showtime.yak.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {
    @Value("${application.redis.host}")
    private String redisHost;
    @Value("${application.redis.port}")
    private int redisPort;
    @Value("${application.redis.password}")
    private String redisPassword;


    public Jedis jedis() {
        Jedis jedis = new Jedis(redisHost, redisPort);
        jedis.auth(redisPassword);
        jedis.connect();
        return jedis;
    }

}