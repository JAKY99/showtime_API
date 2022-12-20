package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.KafkaMessageDto;
import com.m2i.showtime.yak.Service.LoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/kafka")
public class KafkaController {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final LoggerService LOGGER = new LoggerService();
    @PostMapping("/send")
    public String sendMessage(@RequestBody KafkaMessageDto kafkaMessageDto) {

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
            }
        });
        return "Sending message to topic: " + kafkaMessageDto.getTopicName() + "With message : " + kafkaMessageDto.getMessage();

    }

}
