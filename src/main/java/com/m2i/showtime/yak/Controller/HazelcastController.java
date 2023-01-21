package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.getDataFromHazelcastDto;
import com.m2i.showtime.yak.Dto.getImageFromHazelcastDto;
import com.m2i.showtime.yak.Service.HazelcastService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("api/v1/hazelcast")
public class HazelcastController {
    private HazelcastService hazelcastService;

    HazelcastController(HazelcastService hazelcastService) {
        this.hazelcastService = hazelcastService;
    }
    @GetMapping("/get/image")
    public getImageFromHazelcastDto getImageFromHazelcast(@RequestParam String urlApi) {
        return hazelcastService.getHazelcastCache(urlApi);
    }
    @PostMapping("/get/data")
    public getDataFromHazelcastDto getDataFromHazelcast(HttpServletResponse response, @RequestBody getImageFromHazelcastDto getImageFromHazelcastDto) throws URISyntaxException, IOException, InterruptedException {
        return hazelcastService.getHazelcastCacheData(getImageFromHazelcastDto.getUrlApi(),response);
    }
}
