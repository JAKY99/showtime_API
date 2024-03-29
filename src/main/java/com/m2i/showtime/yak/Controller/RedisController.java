package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.getDataFromRedisDto;
import com.m2i.showtime.yak.Dto.getImageFromRedisDto;
import com.m2i.showtime.yak.Service.RedisService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("api/v1/redis")
public class RedisController {
    private RedisService redisService;

    RedisController(RedisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping ("/get/image")
    public getImageFromRedisDto getImageFromRedis(HttpServletResponse response,@RequestParam String urlApi) {
        return redisService.getRedisCache(urlApi);
    }
    @GetMapping("/get/data")
    public getDataFromRedisDto getDataFromRedis(HttpServletResponse response,@RequestParam String urlApi) throws URISyntaxException, IOException, InterruptedException {
        return redisService. getRedisCacheData(urlApi,response);
    }
}
