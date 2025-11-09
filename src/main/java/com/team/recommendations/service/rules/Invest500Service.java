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

/**
 * Service for checking if the user is to be recommended Invest 500 Product based on static rules
 * Static rules are:
 * - Пользователь использует как минимум один продукт с типом DEBIT.
 * - Пользователь не использует продукты с типом INVEST.
 * - Сумма пополнений продуктов с типом SAVING больше 1000 ₽.
 *
 * @author dlok
 * @version 1.0
 */
@Service
public class Invest500Service implements RuleSetService {
    private final RecommendationsRepository recommendationsRepository;

    private final static String INVEST_500_NAME = "Invest 500";
    private final static UUID INVEST_500_ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");
    private final static String INVEST_500_DESCRIPTION = "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!";

    public Invest500Service(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    Logger logger = LoggerFactory.getLogger(Invest500Service.class);

    /**
     * Method to create recommendation if the static rules are followed
     * @param id id of the user to whom the recommendation is being made
     * @return returns recommendation if the static rules are followed or null if not
     * @see #isGettingRecommendation(UUID)
     */
    @Override
    public Optional<Recommendation> getRecommendation(UUID id) {
        logger.info("Adding Invest500 function was invoked for user: {}", id);
        Recommendation recommendation = null;
        if (isGettingRecommendation(id)) {
            recommendation = new Recommendation(INVEST_500_NAME,INVEST_500_ID,INVEST_500_DESCRIPTION);
        }
        return Optional.ofNullable(recommendation);
    }

    /**
     * Methods checks if all the static rules for the product are followed
     * @param id id of the user to whom the recommendation is being made
     * @return true is the rules are followed
     */
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
