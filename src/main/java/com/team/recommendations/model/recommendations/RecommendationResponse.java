package com.team.recommendations.model.recommendations;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class RecommendationResponse {
    private final UUID user_id;
    private final Collection<Recommendation> recommendations;

    public RecommendationResponse(UUID user_id, Collection<Recommendation> recommendations) {
        this.user_id = user_id;
        this.recommendations = recommendations;
    }

    public UUID getUser_id() {
        return user_id;
    }

    public Collection<Recommendation> getRecommendations() {
        return recommendations;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecommendationResponse that = (RecommendationResponse) o;
        return Objects.equals(user_id, that.user_id) && Objects.equals(recommendations, that.recommendations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, recommendations);
    }

    @Override
    public String toString() {
        return "RecommendationResponse{" +
                "user_id=" + user_id +
                ", recommendations=" + recommendations +
                '}';
    }
}
