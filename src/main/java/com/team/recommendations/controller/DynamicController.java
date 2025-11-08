package com.team.recommendations.controller;

import com.team.recommendations.model.dynamic.Data;
import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.model.stats.Stats;
import com.team.recommendations.service.DynamicService;
import com.team.recommendations.service.StatsService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class DynamicController {
    private final DynamicService dynamicService;
    private final StatsService statsService;

    public DynamicController(DynamicService dynamicService, StatsService statsService) {
        this.dynamicService = dynamicService;
        this.statsService = statsService;
    }

    @GetMapping("/rule")
    public Data getAllProducts() {
        return new Data(dynamicService.getAllProducts());
    }

    @PostMapping("/rule")
    public DynamicProduct addProduct(@RequestBody DynamicProduct product) {
        return dynamicService.addProduct(product);
    }

    @DeleteMapping("/rule/{product_id}")
    public void deleteProduct(@PathVariable UUID product_id) {
        dynamicService.deleteProduct(product_id);
    }

    @GetMapping("/rule/stats")
    public Stats getRuleStats() {
        return statsService.getAllStats();
    }

    @DeleteMapping("/rule/stats")
    public void deleteRuleStats() {
        statsService.deleteStats();
    }
}
