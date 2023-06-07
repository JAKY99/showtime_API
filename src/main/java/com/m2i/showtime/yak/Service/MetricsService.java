package com.m2i.showtime.yak.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2i.showtime.yak.Dto.MessageAdminDto;
import com.m2i.showtime.yak.Dto.MetricsDto;
import com.m2i.showtime.yak.Entity.Metrics;
import com.m2i.showtime.yak.Repository.MetricsRepository;
import com.m2i.showtime.yak.Repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class MetricsService {
    private final MetricsRepository metricsRepository;
    private final UserRepository userRepository;
    private final KafkaMessageGeneratorService kafkaMessageGeneratorService;

    public MetricsService(MetricsRepository metricsRepository,
                          UserRepository userRepository, KafkaMessageGeneratorService kafkaMessageGeneratorService) {
        this.metricsRepository = metricsRepository;
        this.userRepository = userRepository;
        this.kafkaMessageGeneratorService = kafkaMessageGeneratorService;
    }

    public List<Metrics> getMetrics() {
        List<Metrics> getLastMetrics = metricsRepository.findLastMetrics();
        return getLastMetrics;
    }
    @Scheduled(fixedRate = 30 ,timeUnit = TimeUnit.MINUTES)
    public boolean updateMetrics() throws JsonProcessingException {
        metricsRepository.deleteAll();
        metricsRepository.resetAutoIncrement();
        Metrics metrics = new Metrics();
        metrics.setTotalUsers((long) userRepository.findAll().size());
        metrics.setTotalConnectedUsers(0L);
        metricsRepository.save(metrics);
        MessageAdminDto messageAdminDto = new MessageAdminDto();
        messageAdminDto.setMessage("metrics updated");
        kafkaMessageGeneratorService.generateMessageToAdmin(messageAdminDto);
        kafkaMessageGeneratorService.checkUserOnline(metrics.getId());
        return true;
    }

    public boolean updateMetricsOnlineUser(MetricsDto metricsInfo) throws JsonProcessingException {
        Optional<Metrics> metrics = metricsRepository.findById(Long.parseLong(metricsInfo.getMetricsId()));
        if (metrics.isPresent()) {
            metrics.get().setTotalConnectedUsers(metrics.get().getTotalConnectedUsers() + 1);
            metricsRepository.save(metrics.get());
            MessageAdminDto messageAdminDto = new MessageAdminDto();
            messageAdminDto.setMessage("metrics updated");
            kafkaMessageGeneratorService.generateMessageToAdmin(messageAdminDto);
            return true;
        }
        return false;
    }
}
