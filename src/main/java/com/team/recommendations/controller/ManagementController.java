package com.team.recommendations.controller;

import com.team.recommendations.model.ServiceInfo;
import com.team.recommendations.service.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Менеджмент", description = "Методы для работы с системой")
@RequestMapping("/management")
public class ManagementController {
    private final CacheService cacheService;

    @Autowired
    private BuildProperties buildProperties;

    public ManagementController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Operation(operationId = "clearCache",
            summary = "Обновление кэша",
            tags = {"Менеджмент"})
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PostMapping("/clear-caches")
    public ResponseEntity<String> clearCache() {
        cacheService.evictAllCaches();
        return ResponseEntity.ok("Кэш сброшен");
    }

    @Operation(operationId = "getServiceInfo",
            summary = "Получение информации о сервисе",
            tags = {"Менеджмент"})
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/info")
    public ServiceInfo getServiceInfo() {
        return new ServiceInfo(
                buildProperties.getArtifact(),
                buildProperties.getVersion()
        );
    }
}
