package com.m2i.showtime.yak.Configuration;

import com.m2i.showtime.yak.Service.LoggerService;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    private final LoggerService LOGGER = new LoggerService();

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {
        LOGGER.print("topic1");
        return new NewTopic("admin", 1, (short) 1);
    }
    @Bean
    public NewTopic topic2() {
        LOGGER.print("topic2");
        return new NewTopic("user", 1, (short) 1);
    }
    @Bean
    public NewTopic topic3() {
        LOGGER.print("elasticsearchUpdate");
        return new NewTopic("elasticsearchUpdate", 1, (short) 1);
    }

}