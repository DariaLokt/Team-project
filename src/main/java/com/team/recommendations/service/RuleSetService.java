package com.team.recommendations.service;

import com.team.recommendations.model.Recommendation;

import java.util.Optional;
import java.util.UUID;

public interface RuleSetService {
    Optional<Recommendation> getRecommendation(UUID id);
    boolean isGettingRecommendation(UUID id);
}
