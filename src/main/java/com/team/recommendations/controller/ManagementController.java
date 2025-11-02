package com.team.recommendations.controller;

import com.team.recommendations.service.CacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
public class ManagementController {
    private final CacheService cacheService;

    public ManagementController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<String> clearCache() {
        cacheService.evictAllCaches();
        return ResponseEntity.ok("Кэш сброшен");
    }
}
