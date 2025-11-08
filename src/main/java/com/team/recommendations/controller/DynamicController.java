package com.team.recommendations.controller;

import com.team.recommendations.model.dynamic.Data;
import com.team.recommendations.model.dynamic.DynamicProductDto;
import com.team.recommendations.model.stats.StatsDto;
import com.team.recommendations.service.rules.DynamicService;
import com.team.recommendations.service.rules.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/rule")
@Tag(name = "Динамические правила", description = "Методы для работы с продуктами с динамическими правилами")
@RestController
public class DynamicController {
    private final DynamicService dynamicService;
    private final StatsService statsService;

    public DynamicController(DynamicService dynamicService, StatsService statsService) {
        this.dynamicService = dynamicService;
        this.statsService = statsService;
    }

    @Operation(operationId = "getAllProducts",
            summary = "Получение всех продуктов с динамическими правилами",
            tags = {"Динамические правила"})
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping
    public Data getAllProducts() {
        return new Data(dynamicService.getAllProducts());
    }

    @Operation(operationId = "addProduct",
            summary = "Добавление нового продукта с динамическими правилами",
            tags = {"Динамические правила"})
    @ApiResponse(responseCode = "201", description = "Created")
    @PostMapping
    public DynamicProductDto addProduct(@RequestBody DynamicProductDto product) {
        return dynamicService.addProduct(product);
    }

    @Operation(operationId = "deleteProduct",
            summary = "Удаление продукта с динамическими правилами",
            tags = {"Динамические правила"})
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found")
    @DeleteMapping("/{product_id}")
    public void deleteProduct(@PathVariable UUID product_id) {
        dynamicService.deleteProduct(product_id);
    }

    @Operation(operationId = "getRuleStats",
            summary = "Получение статистики по использованию динамических правил",
            tags = {"Динамические правила"})
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/stats")
    public StatsDto getRuleStats() {
        return statsService.getAllStats();
    }

    @Operation(operationId = "deleteRuleStats",
            summary = "Обнуление статистики по использованию динамических правил",
            tags = {"Динамические правила"})
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found")
    @DeleteMapping("/stats")
    public void deleteRuleStats() {
        statsService.deleteStats();
    }
}
