package com.team.recommendations.controller;

import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.service.DynamicService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
public class DynamicController {
    private final DynamicService dynamicService;

    public DynamicController(DynamicService dynamicService) {
        this.dynamicService = dynamicService;
    }

    @GetMapping("/rule")
    public Collection<DynamicProduct> getAllProducts() {
        return dynamicService.getAllProducts();
    }

    @PostMapping("/rule")
    public DynamicProduct addProduct(@RequestBody DynamicProduct product) {
        return dynamicService.addProduct(product);
    }

    @DeleteMapping("/rule/{product_id}")
    public void deleteProduct(@PathVariable UUID product_id) {
        dynamicService.deleteProduct(product_id);
    }
}
