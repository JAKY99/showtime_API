package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.CommentNotifDto;
import com.m2i.showtime.yak.Dto.KafkaMessageDto;
import com.m2i.showtime.yak.Dto.KafkaResponseDto;
import com.m2i.showtime.yak.Service.KafkaMessageGeneratorService;
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
    public KafkaResponseDto sendMessage(@RequestBody KafkaMessageDto kafkaMessageDto) {
        return kafkaMessageGeneratorService.sendMessage(kafkaMessageDto);
    }

    @PostMapping("/commentNotif")
    public void sendCommentNotif(@RequestBody CommentNotifDto commentNotifDto) {
        kafkaMessageGeneratorService.sendCommentNotif(commentNotifDto);
    }

}
