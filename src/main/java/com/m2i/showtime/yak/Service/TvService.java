package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Configuration.RedisConfig;
import com.m2i.showtime.yak.Entity.Serie;
import com.m2i.showtime.yak.Repository.TvRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TvService {
    private final UserRepository userRepository;
    private final RedisConfig redisConfig;
    private  final TvRepository tvRepository;

    public TvService(UserRepository userRepository, RedisConfig redisConfig, TvRepository tvRepository) {
        this.userRepository = userRepository;
        this.redisConfig = redisConfig;
        this.tvRepository = tvRepository;
    }

    public List<Serie> getSeries() {
        return tvRepository.findAll();
    }

}
