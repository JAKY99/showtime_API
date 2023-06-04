package com.m2i.showtime.yak.Service;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URISyntaxException;


@Getter
@Setter
@Service
public class KafkaListenerService {
    private SimpMessagingTemplate simpMessagingTemplate;
    private final ElasticsearchService elasticSearchService;
    private final LoggerService LOGGER = new LoggerService();
    @Value("${spring.profiles.active}")
    private String env;
    @Autowired
    public KafkaListenerService(ElasticsearchService elasticSearchService, SimpMessagingTemplate simpMessagingTemplate) {
        this.elasticSearchService = elasticSearchService;
        this.simpMessagingTemplate = simpMessagingTemplate;
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

    @KafkaListener(topics = "${spring.profiles.active}UserComment", groupId = "${spring.profiles.active}")
    public void listenCommentUser(String message) {
        String userName = message.split("/")[1];
        String messageToSend = message.split("/")[0];
        simpMessagingTemplate.convertAndSend("/topic/user/"+env+"/"+userName, messageToSend);
        LOGGER.print("Received Message in group " + env + " : " + message);
    }
    @KafkaListener(topics = "${spring.profiles.active}UserNotificationService", groupId = "${spring.profiles.active}")
    public void listenUserNotificationService(String message){
        JSONObject data = new JSONObject(message);
        LOGGER.print("Received Message in group " + env +"UserNotificationService : " + data.toString());
        simpMessagingTemplate.convertAndSend("/topic/usernotification/"+env+"/"+data.get("target"), data.toString());
    }
    @KafkaListener(topics = "${spring.profiles.active}UserNotificationToAllUsersService", groupId = "${spring.profiles.active}")
    public void listenUserNotificationToAllUsersService(String message){
        JSONObject data = new JSONObject(message);
        LOGGER.print("Received Message in group " + env +"UserNotificationService : " + data.toString());
        simpMessagingTemplate.convertAndSend("/topic/usernotification/"+env, data.toString());
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
    @KafkaListener(topics = "${spring.profiles.active}PingUser", groupId = "${spring.profiles.active}")
    public void checkUsersOnline(String message) {
        JSONObject data = new JSONObject();
        data.put("metrics_id", message);
        simpMessagingTemplate.convertAndSend("/topic/user/ping/"+env, data.toString());
    }
    @KafkaListener(topics = "${spring.profiles.active}UpdateAppUser", groupId = "${spring.profiles.active}")
    public void notificationUpdateApp(String message) {
        JSONObject data = new JSONObject(message);
        LOGGER.print("Received Message in group " + env +"UserNotificationService : " + data.toString());
        simpMessagingTemplate.convertAndSend("/topic/update/"+env, data.toString());
    }
}
