package com.team.recommendations.service.rules;

import com.team.recommendations.model.recommendations.Recommendation;

import java.util.Optional;
import java.util.UUID;

public interface RuleSetService {
    Optional<Recommendation> getRecommendation(UUID id);
    boolean isGettingRecommendation(UUID id);
}
