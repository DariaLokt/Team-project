package com.team.recommendations.service;

import com.team.recommendations.model.Recommendation;
import com.team.recommendations.repository.RecommendationsRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class Invest500Service implements RuleSetService{
    private final RecommendationsRepository recommendationsRepository;

    public Invest500Service(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    @Override
    public Optional<Recommendation> getRecommendation(UUID id) {
        Recommendation recommendation = null;
        if (isGettingRecommendation(id)) {
            recommendation = new Recommendation("Invest 500", UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"), "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!");
        }
        return Optional.ofNullable(recommendation);
    }

    @Override
    public boolean isGettingRecommendation(UUID id) {
//        rule1

//        rule2

//        rule3

        return true;
    }
}
