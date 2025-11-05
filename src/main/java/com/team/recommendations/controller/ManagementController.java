package com.team.recommendations.controller;

import com.team.recommendations.model.ServiceInfo;
import com.team.recommendations.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
public class ManagementController {
    private final CacheService cacheService;

    @Autowired
    private BuildProperties buildProperties;

    public ManagementController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<String> clearCache() {
        cacheService.evictAllCaches();
        return ResponseEntity.ok("Кэш сброшен");
    }

    @GetMapping("/info")
    public ServiceInfo getServiceInfo() {
        return new ServiceInfo(
                buildProperties.getArtifact(),
                buildProperties.getVersion()
        );
    }
}
