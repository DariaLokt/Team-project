package com.team.recommendations.service.rules;

import com.team.recommendations.model.recommendations.Recommendation;
import com.team.recommendations.model.rules.CompareRule;
import com.team.recommendations.model.rules.IfUsedRule;
import com.team.recommendations.repository.RecommendationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class Invest500Service implements RuleSetService {
    private final RecommendationsRepository recommendationsRepository;

    public Invest500Service(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    Logger logger = LoggerFactory.getLogger(Invest500Service.class);

    @Override
    public Optional<Recommendation> getRecommendation(UUID id) {
        logger.info("Adding Invest500 function was invoked for user: {}", id);
        Recommendation recommendation = null;
        if (isGettingRecommendation(id)) {
            recommendation = new Recommendation("Invest 500", UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"), "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!");
        }
        return Optional.ofNullable(recommendation);
    }

    @Override
    public boolean isGettingRecommendation(UUID id) {
//        rule1
        IfUsedRule rule1 = new IfUsedRule(recommendationsRepository.getUse(id,"DEBIT"),true);
//        rule2
        IfUsedRule rule2 = new IfUsedRule(recommendationsRepository.getUse(id,"INVEST"), false);
//        rule3
        CompareRule rule3 = new CompareRule(recommendationsRepository.getTransactionSum(id,"SAVING","DEPOSIT"),">",1000);

        logger.info("Invest500 was checked for user: {}", id);

        return rule1.isFollowed() && rule2.isFollowed() && rule3.isFollowed();
    }
}
