package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Service.RedisService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/redis")
public class RedisController {
    private RedisService redisService;

    RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @PostMapping("/get/image")
    public String getImageFromRedis(String urlApi){
        return redisService.getRedisCache(urlApi);
    }
}
