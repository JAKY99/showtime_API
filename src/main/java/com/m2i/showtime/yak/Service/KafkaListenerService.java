package com.m2i.showtime.yak.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@EnableAsync
public class KafkaListenerService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @KafkaListener(topics = "topicName")
    public void listenGroupFoo(String message) {
        System.out.println("Received Message in group foo: " + message);
    }

    @KafkaListener(topics = "admin")
    public void listenGroupFooAdmin(String message) {
        simpMessagingTemplate.convertAndSend("/topic/admin", message);
        System.out.println("Received Message in group admin: " + message);
    }

    @KafkaListener(topics = "user")
    public void listenGroupFooUser(String message) {
        simpMessagingTemplate.convertAndSend("/topic/user", message);
        System.out.println("Received Message in group user: " + message);
    }
    @KafkaListener(topics = "activity")
    public void listenGroupFooActivity(String message) {
        simpMessagingTemplate.convertAndSend("/topic/activity", message);
        System.out.println("Received Message in group user: " + message);
    }
    @Scheduled(fixedRate = 10000)
    @Async
    public void getSchedulersActivity () {
        System.out.println("Activity Scheduler");
        simpMessagingTemplate.convertAndSend("/topic/activity", "Still active");
    }
}
