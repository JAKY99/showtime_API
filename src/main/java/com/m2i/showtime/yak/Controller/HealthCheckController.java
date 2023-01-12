package com.m2i.showtime.yak.Controller;

import com.m2i.showtime.yak.Dto.HealthCheckStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/health")
public class HealthCheckController {
    @GetMapping("/check")
    public HealthCheckStatus healthCheck() {
        return new HealthCheckStatus();
    }
}
