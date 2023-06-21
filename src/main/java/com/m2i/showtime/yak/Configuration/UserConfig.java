package com.m2i.showtime.yak.Configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class UserConfig {

    @Bean
    @Qualifier("ThreadPoolTaskExecutorCustom")
    public ThreadPoolTaskExecutor taskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1); // Set the core pool size as per your requirement
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors()); // Set the maximum pool size as per your requirement
        executor.setKeepAliveSeconds(10); // Set the keep alive parameter as per your requirement
        executor.setQueueCapacity(25); // Set the queue capacity as per your requirement
        executor.initialize();
        return executor;
    }
}
