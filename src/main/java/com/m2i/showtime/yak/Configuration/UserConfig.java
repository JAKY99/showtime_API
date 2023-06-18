package com.m2i.showtime.yak.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class UserConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Set the core pool size as per your requirement
        executor.setMaxPoolSize(10); // Set the maximum pool size as per your requirement
        executor.setQueueCapacity(25); // Set the queue capacity as per your requirement
        executor.initialize();
        return executor;
    }
}
