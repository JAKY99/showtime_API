package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.*;
import com.m2i.showtime.yak.Entity.Movie;
import com.m2i.showtime.yak.Repository.MovieRepository;
import com.m2i.showtime.yak.Service.CustomThreadService;
import com.m2i.showtime.yak.Service.MovieService;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
