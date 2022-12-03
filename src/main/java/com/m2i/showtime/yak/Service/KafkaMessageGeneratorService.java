package com.m2i.showtime.yak.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2i.showtime.yak.Dto.KafkaMessageDto;
import com.m2i.showtime.yak.Dto.MessageAdminDto;
import com.m2i.showtime.yak.Entity.Notification;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.NotificationRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.Optional;

@Service
public class KafkaMessageGeneratorService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final NotificationRepository notificationRepository;
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
                System.out.println("Sent message=[" + kafkaMessageDto.getMessage() +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=["
                        + kafkaMessageDto.getMessage() + "] due to : " + ex.getMessage());
            }
        });
    }

    public String generateAlertToAdmin(String message,String severity) {
        return message + " " + severity;
    }
}
