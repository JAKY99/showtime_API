package com.m2i.showtime.yak.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2i.showtime.yak.Dto.CommentNotifDto;
import com.m2i.showtime.yak.Dto.KafkaMessageDto;
import com.m2i.showtime.yak.Dto.KafkaResponseDto;
import com.m2i.showtime.yak.Dto.MessageAdminDto;
import com.m2i.showtime.yak.Entity.Notification;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.NotificationRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class KafkaMessageGeneratorService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final NotificationRepository notificationRepository;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private KafkaAdmin kafkaAdmin;
    private final LoggerService LOGGER = new LoggerService();
    public KafkaMessageGeneratorService(UserRepository userRepository, NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }
    public void generateMessageToAdmin(MessageAdminDto message) throws JsonProcessingException {

        Optional<User[]> userList = this.userRepository.findAllAdminUsers();

        userList.ifPresent(users -> {
            for (User user : users) {
                Notification notification = new Notification(message.getMessage(), message.getSeverity(),"alert");
                this.notificationRepository.save(notification);
                user.getNotifications().add(notification);
                this.userRepository.save(user);
            }
        });
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonMessage = ow.writeValueAsString(message);
        KafkaMessageDto kafkaMessageDto = new KafkaMessageDto( jsonMessage,"admin");
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(kafkaMessageDto.getTopicName(), kafkaMessageDto.getMessage());
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOGGER.print("Sent message=[" + kafkaMessageDto.getMessage() +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.print("Unable to send message=["
                        + kafkaMessageDto.getMessage() + "] due to : " + ex.getMessage());
            }
        });
    }

    public String generateAlertToAdmin(String message,String severity) {
        return message + " " + severity;
    }
    public KafkaResponseDto sendMessage(KafkaMessageDto kafkaMessageDto) {

        LOGGER.print("Sending message to topic: " + kafkaMessageDto.getTopicName());
        LOGGER.print("With message : " + kafkaMessageDto.getMessage());
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(kafkaMessageDto.getTopicName(), kafkaMessageDto.getMessage());

        simpMessagingTemplate.convertAndSend("/web-socket/activity", "Still active");
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOGGER.print("Sent message=[" + kafkaMessageDto.getMessage() +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");

            }
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.print("Unable to send message=["
                        + kafkaMessageDto.getMessage() + "] due to : " + ex.getMessage());
                AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties());
                client.close();
                kafkaAdmin.initialize();
            }
        });
        return new KafkaResponseDto("Sending message to topic: " + kafkaMessageDto.getTopicName() + "With message : " + kafkaMessageDto.getMessage());
    }

    public void sendCommentNotif(CommentNotifDto commentNotifDto) {


        LOGGER.print("Sending message to topic: " + commentNotifDto.getTopicName());
        LOGGER.print("With message : " + commentNotifDto.getMessage());
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(commentNotifDto.getTopicName(), commentNotifDto.getMessage() + "/" + commentNotifDto.getUsername());

        simpMessagingTemplate.convertAndSend("/web-socket/activity", "Still active");
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOGGER.print("Sent message=[" + commentNotifDto.getMessage() +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");

            }
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.print("Unable to send message=["
                        + commentNotifDto.getMessage() + "] due to : " + ex.getMessage());
                AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties());
                client.close();
                kafkaAdmin.initialize();
            }
        });
    }

    public boolean sendNotification(User user, Notification notification,String topicName) throws JSONException {
        LOGGER.print("Sending message to topic: " + user.getUsername());
        LOGGER.print("With message : " + notification.getMessage());
        JSONObject data = new JSONObject();
        data.put("message", notification.getMessage());
        data.put("severity", notification.getSeverity());
        data.put("type", notification.getType());
        data.put("id", notification.getId());
        data.put("dateCreated", notification.getDateCreated());
        data.put("read", notification.getStatus());
        data.put("dateRead", notification.getDateRead());
        data.put("target", user.getUsername());

        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(topicName, data.toString());

        simpMessagingTemplate.convertAndSend("/web-socket/activity", "Still active");
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOGGER.print("Sent message=[" + notification.getMessage() +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");

            }
            @Override
            public void onFailure(Throwable ex) {
                LOGGER.print("Unable to send message=["
                        + notification.getMessage() + "] due to : " + ex.getMessage());
                AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties());
                client.close();
                kafkaAdmin.initialize();
            }
        });
        return true;
    }
}
