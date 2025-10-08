package com.team.recommendations.controller;

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

    public RecommendationController(Invest500Service invest500Service, TopSavingService topSavingService, SimpleCreditService simpleCreditService) {
        this.invest500Service = invest500Service;
        this.topSavingService = topSavingService;
        this.simpleCreditService = simpleCreditService;
    }

    @GetMapping("/recommendation/{user_id}")
    public void getUserRecommendations() {

    }
}
