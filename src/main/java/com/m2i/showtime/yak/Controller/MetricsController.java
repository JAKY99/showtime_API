package com.m2i.showtime.yak.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2i.showtime.yak.Dto.MetricsDto;
import com.m2i.showtime.yak.Entity.Metrics;
import com.m2i.showtime.yak.Service.MetricsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN, ROLE_USER')")
@RequestMapping(path = "api/v1/metrics")
public class MetricsController {

    private final MetricsService metricsService;
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }
    @PostMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public List<Metrics> getMetrics() {
        return metricsService.getMetrics();
    }
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public boolean updateMetrics() throws JsonProcessingException {
        return metricsService.updateMetrics();
    }
    @PostMapping("/ping")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public boolean updateMetrics(@RequestBody MetricsDto metricsInfo) throws JsonProcessingException {
        return metricsService.updateMetricsOnlineUser(metricsInfo);
    }
}
