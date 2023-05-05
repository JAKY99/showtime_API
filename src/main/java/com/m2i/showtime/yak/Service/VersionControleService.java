package com.m2i.showtime.yak.Service;

import com.m2i.showtime.yak.Dto.KafkaMessageDto;
import com.m2i.showtime.yak.Dto.VersionControlDto;
import com.m2i.showtime.yak.Dto.responseAndroidVersionDto;
import com.m2i.showtime.yak.Entity.VersionControle;
import com.m2i.showtime.yak.Repository.VersionControleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class VersionControleService {
    private final VersionControleRepository versionControleRepository;
    @Value ("${version.code.android}")
    private String versionCodeAndroid;
    @Value ("${version.name.android}")
    private String versionNameAndroid;
    @Value("${spring.profiles.active}")
    private String env;
    private final KafkaMessageGeneratorService kafkaMessageGeneratorService;
    public VersionControleService(VersionControleRepository versionControleRepository, KafkaMessageGeneratorService kafkaMessageGeneratorService) {
        this.versionControleRepository = versionControleRepository;
        this.kafkaMessageGeneratorService = kafkaMessageGeneratorService;
    }

    public VersionControlDto getVersion(String type) {
        VersionControlDto versionControlDto = new VersionControlDto();
        LocalDateTime now = java.time.LocalDateTime.now();
        Page<VersionControle> result = versionControleRepository
                .findByType(type, PageRequest.of(0, 1));
        if (result.getTotalElements() > 0) {
            String checkResult = result.getContent().isEmpty() ? now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : result.getContent().get(0).getVersion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            versionControlDto.setVersion(checkResult);
            return versionControlDto;
        }
        if(result.getTotalElements() == 0){

            VersionControle versionControle = new VersionControle();
            versionControle.setType(type);
            versionControle.setVersion(now);
            versionControleRepository.save(versionControle);
            versionControlDto.setVersion(now.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            return versionControlDto;
        }
        return null;
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

    public responseAndroidVersionDto getAndroidVersion() {
        responseAndroidVersionDto responseAndroidVersionDto = new responseAndroidVersionDto();
        responseAndroidVersionDto.setVersionCode(versionCodeAndroid);
        responseAndroidVersionDto.setVersionName(versionNameAndroid);
        return responseAndroidVersionDto;
    }
}
