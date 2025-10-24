package com.team.recommendations.controller;

import com.team.recommendations.model.recommendations.Recommendation;
import com.team.recommendations.model.recommendations.RecommendationResponse;
import com.team.recommendations.service.DynamicRecommendationService;
import com.team.recommendations.service.Invest500Service;
import com.team.recommendations.service.SimpleCreditService;
import com.team.recommendations.service.TopSavingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

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

    @GetMapping("/recommendation/{user_id}")
    public RecommendationResponse getUserRecommendations(@PathVariable("user_id")UUID user_id) {
        Collection<Recommendation> recommendations = new ArrayList<>();
//        if (invest500Service.isGettingRecommendation(user_id)) быстрее ли?
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

    @GetMapping("/recommendation/dynamic/{user_id}")
    public RecommendationResponse getUserDynamicRecommendations(@PathVariable("user_id")UUID user_id) {
        Collection<Recommendation> recommendations = dynamicRecommendationService.getRecommendations(user_id);
        return new RecommendationResponse(user_id,recommendations);
    }
}
