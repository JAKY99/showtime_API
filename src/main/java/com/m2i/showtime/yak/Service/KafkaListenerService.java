package com.m2i.showtime.yak.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
@EnableScheduling
@EnableAsync
public class KafkaListenerService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final ElasticsearchService elasticSearchService;
    private final LoggerService LOGGER = new LoggerService();

    public KafkaListenerService(ElasticsearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @KafkaListener(topics = "topicName")
    public void listenGroupFoo(String message) {
        LOGGER.print("Received Message in group foo: " + message);
    }

    @KafkaListener(topics = "admin")
    public void listenGroupFooAdmin(String message) {
        simpMessagingTemplate.convertAndSend("/topic/admin", message);
        LOGGER.print("Received Message in group admin: " + message);
    }

    @KafkaListener(topics = "elasticsearchUpdate")
    public void listenGroupFooElasticsearchUpdate(String message) throws IOException, URISyntaxException, InterruptedException {
        elasticSearchService.dailyUpdate();
    }

    @KafkaListener(topics = "user")
    public void listenGroupFooUser(String message) {
        simpMessagingTemplate.convertAndSend("/topic/user", message);
        LOGGER.print("Received Message in group user: " + message);
    }
    @KafkaListener(topics = "activity")
    public void listenGroupFooActivity(String message) {
        simpMessagingTemplate.convertAndSend("/topic/activity", message);
        LOGGER.print("Received Message in group user: " + message);
    }
    @Scheduled(fixedRate = 10000)
    @Async
    public void getSchedulersActivity () {
        LOGGER.print("Activity Scheduler");
        simpMessagingTemplate.convertAndSend("/topic/activity", "Still active");
    }
}
