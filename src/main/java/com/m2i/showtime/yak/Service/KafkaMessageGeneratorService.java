package com.m2i.showtime.yak.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2i.showtime.yak.Dto.KafkaMessageDto;
import com.m2i.showtime.yak.Dto.MessageAdminDto;
import com.m2i.showtime.yak.Entity.Notification;
import com.m2i.showtime.yak.Entity.User;
import com.m2i.showtime.yak.Repository.NotificationRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String sendMessage(KafkaMessageDto kafkaMessageDto) {

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
        return "Sending message to topic: " + kafkaMessageDto.getTopicName() + "With message : " + kafkaMessageDto.getMessage();
    }
}
