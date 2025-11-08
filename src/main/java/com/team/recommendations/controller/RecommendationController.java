package com.team.recommendations.controller;

import com.team.recommendations.model.recommendations.Recommendation;
import com.team.recommendations.model.recommendations.RecommendationResponse;
import com.team.recommendations.service.DynamicRecommendationService;
import com.team.recommendations.service.rules.Invest500Service;
import com.team.recommendations.service.rules.SimpleCreditService;
import com.team.recommendations.service.rules.TopSavingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@RequestMapping("/recommendation")
@Tag(name = "Рекомендации", description = "Методы для получения рекомендаций")
@RestController
public class RecommendationController {
    private final Invest500Service invest500Service;
    private final TopSavingService topSavingService;
    private final SimpleCreditService simpleCreditService;
    private final DynamicRecommendationService dynamicRecommendationService;

    public RecommendationController(Invest500Service invest500Service, TopSavingService topSavingService, SimpleCreditService simpleCreditService, DynamicRecommendationService dynamicRecommendationService) {
        this.invest500Service = invest500Service;
        this.topSavingService = topSavingService;
        this.simpleCreditService = simpleCreditService;
        this.dynamicRecommendationService = dynamicRecommendationService;
    }

    @Operation(operationId = "getUserRecommendations",
            summary = "Получение рекомендаций со статическими правилами",
            tags = {"Рекомендации"})
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{user_id}")
    public RecommendationResponse getUserRecommendations(@PathVariable("user_id")UUID user_id) {
        Collection<Recommendation> recommendations = new ArrayList<>();
        if (invest500Service.getRecommendation(user_id).isPresent())
        {
            recommendations.add(invest500Service.getRecommendation(user_id).get());
        }
        if (topSavingService.getRecommendation(user_id).isPresent()) {
            recommendations.add(topSavingService.getRecommendation(user_id).get());
        }
        if (simpleCreditService.getRecommendation(user_id).isPresent())
        {
            recommendations.add(simpleCreditService.getRecommendation(user_id).get());
        }
        return new RecommendationResponse(user_id,recommendations);
    }

    @Operation(operationId = "getUserDynamicRecommendations",
            summary = "Получение рекомендаций с динамическими правилами",
            tags = {"Рекомендации"})
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/dynamic/{user_id}")
    public RecommendationResponse getUserDynamicRecommendations(@PathVariable("user_id")UUID user_id) {
        Collection<Recommendation> recommendations = dynamicRecommendationService.getRecommendations(user_id);
        return new RecommendationResponse(user_id,recommendations);
    }
}
