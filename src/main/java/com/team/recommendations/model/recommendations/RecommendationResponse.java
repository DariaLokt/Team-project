package com.team.recommendations.model.recommendations;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class RecommendationResponse {
    private final UUID userId;
    private final Collection<Recommendation> recommendations;

    public RecommendationResponse(UUID userId, Collection<Recommendation> recommendations) {
        this.userId = userId;
        this.recommendations = recommendations;
    }

    public UUID getUserId() {
        return userId;
    }

    public Collection<Recommendation> getRecommendations() {
        return recommendations;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecommendationResponse that = (RecommendationResponse) o;
        return Objects.equals(userId, that.userId) && Objects.equals(recommendations, that.recommendations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, recommendations);
    }

    @Override
    public String toString() {
        return "RecommendationResponse{" +
                "user_id=" + userId +
                ", recommendations=" + recommendations +
                '}';
    }
}
