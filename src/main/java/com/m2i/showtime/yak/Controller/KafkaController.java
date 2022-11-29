package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.KafkaMessageDto;
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
    @PostMapping("/send")
    public int sendMessage(@RequestBody KafkaMessageDto kafkaMessageDto) {

        System.out.println("Sending message to topic: " + kafkaMessageDto.getTopicName());
        System.out.println("Sending message to topic: " + kafkaMessageDto.getMessage());
        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(kafkaMessageDto.getTopicName(), kafkaMessageDto.getMessage());
        System.out.println("Message sent successfully");
        simpMessagingTemplate.convertAndSend("/web-socket/activity", "Still active");
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

        return 1;
    }

}
