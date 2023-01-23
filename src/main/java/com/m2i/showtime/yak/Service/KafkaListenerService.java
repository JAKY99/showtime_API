package com.m2i.showtime.yak.Service;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Getter
@Setter
public class KafkaListenerService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final ElasticsearchService elasticSearchService;
    private final LoggerService LOGGER = new LoggerService();
    @Value("${spring.profiles.active}")
    private String env;

    public KafkaListenerService(ElasticsearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @KafkaListener(topics = "topicName")
    public void listenGroupFoo(String message) {
        LOGGER.print("Received Message in group foo: " + message);
    }

    @KafkaListener(topics = "${spring.profiles.active}Admin", groupId = "${spring.profiles.active}")
    public void listenGroupFooAdmin(String message) {
        simpMessagingTemplate.convertAndSend("/topic/admin/"+env, message);
        LOGGER.print("Received Message in group " + env + " : " + message);
    }
    @KafkaListener(topics = "elasticsearchUpdate")
    public void listenGroupFooElasticsearchUpdate(String message) throws IOException, URISyntaxException, InterruptedException {
        elasticSearchService.dailyUpdate();
    }

    @KafkaListener(topics = "${spring.profiles.active}User", groupId = "${spring.profiles.active}")
    public void listenGroupFooUser(String message) {
        simpMessagingTemplate.convertAndSend("/topic/user/"+env, message);
        LOGGER.print("Received Message in group " + env + " : " + message);
    }
    @KafkaListener(topics = "activity")
    public void listenGroupFooActivity(String message) {
        simpMessagingTemplate.convertAndSend("/topic/activity", message);
        LOGGER.print("Received Message in group " + env + " : " + message);
    }
//    @Scheduled(fixedRate = 10000)
//    @Async
//    public void getSchedulersActivity () {
//        LOGGER.print("Activity Scheduler");
//        simpMessagingTemplate.convertAndSend("/topic/activity", "Still active");
//    }
}
