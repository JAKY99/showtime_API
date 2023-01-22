package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.KafkaMessageDto;
import com.m2i.showtime.yak.Service.KafkaMessageGeneratorService;
import com.m2i.showtime.yak.Service.LoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/kafka")
public class KafkaController {
    private final KafkaMessageGeneratorService kafkaMessageGeneratorService;
    public KafkaController(KafkaMessageGeneratorService kafkaMessageGeneratorService) {
        this.kafkaMessageGeneratorService = kafkaMessageGeneratorService;
    }
    @PostMapping("/send")
    public String sendMessage(@RequestBody KafkaMessageDto kafkaMessageDto) {
        return kafkaMessageGeneratorService.sendMessage(kafkaMessageDto);
    }

}
