package com.team.recommendations.controller;

import com.team.recommendations.model.dynamic.Data;
import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.model.dynamic.Stats;
import com.team.recommendations.repository.StatsRepository;
import com.team.recommendations.service.DynamicService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class DynamicController {
    private final DynamicService dynamicService;
    private final StatsRepository statsRepository;

    public DynamicController(DynamicService dynamicService, StatsRepository statsRepository) {
        this.dynamicService = dynamicService;
        this.statsRepository = statsRepository;
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
        return new Stats(statsRepository.findAll());
    }
}
