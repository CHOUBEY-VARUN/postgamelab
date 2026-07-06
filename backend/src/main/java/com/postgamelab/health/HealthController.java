package com.postgamelab.health;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, Object> healthCheck() {
        return Map.of(
                "status", "ok",
                "app", "PostGameLab",
                "timestamp", Instant.now().toString()
        );
    }
} 