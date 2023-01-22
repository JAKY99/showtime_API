package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Dto.KafkaMessageDto;
import com.m2i.showtime.yak.Entity.VersionControle;
import com.m2i.showtime.yak.Repository.VersionControleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class VersionControleService {
    private final VersionControleRepository versionControleRepository;
    @Value("${spring.profiles.active}")
    private String env;
    private final KafkaMessageGeneratorService kafkaMessageGeneratorService;
    public VersionControleService(VersionControleRepository versionControleRepository, KafkaMessageGeneratorService kafkaMessageGeneratorService) {
        this.versionControleRepository = versionControleRepository;
        this.kafkaMessageGeneratorService = kafkaMessageGeneratorService;
    }

    public String getVersion(String type) {
        return   versionControleRepository
                .findByType(type, PageRequest.of(0, 1))
                .getContent()
                .stream()
                .findFirst()
                .get()
                .getVersion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
    public boolean addVersion(String type) {
        VersionControle versionControle = new VersionControle();
        versionControle.setType(type);
        versionControle.setVersion(java.time.LocalDateTime.now());
        versionControleRepository.save(versionControle);
        KafkaMessageDto kafkaMessageDto = new KafkaMessageDto();
        kafkaMessageDto.setTopicName(env + "User");
        kafkaMessageDto.setMessage("New update");
        kafkaMessageGeneratorService.sendMessage(kafkaMessageDto);
        return true;
    }
}
