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
 * Service for checking if the user is to be recommended Simple Credit Product based on static rules
 * Static rules are:
 * - Пользователь не использует продукты с типом CREDIT.
 * - Сумма пополнений по всем продуктам типа DEBIT больше, чем сумма трат по всем продуктам типа DEBIT.
 * - Сумма трат по всем продуктам типа DEBIT больше, чем 100 000 ₽.
 *
 * @author dlok
 * @version 1.0
 */
@Service
public class SimpleCreditService implements RuleSetService {
    private final RecommendationsRepository recommendationsRepository;

    private final static String SIMPLE_CREDIT_NAME = "Простой кредит";
    private final static UUID SIMPLE_CREDIT_ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");
    private final static String SIMPLE_CREDIT_DESCRIPTION = "Откройте мир выгодных кредитов с нами!\n" +
            "\n" +
            "Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.\n" +
            "\n" +
            "Почему выбирают нас:\n" +
            "\n" +
            "Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.\n" +
            "\n" +
            "Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.\n" +
            "\n" +
            "Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.\n" +
            "\n" +
            "Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!";

    public SimpleCreditService(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    Logger logger = LoggerFactory.getLogger(SimpleCreditService.class);

    /**
     * Method to create recommendation if the static rules are followed
     * @param id id of the user to whom the recommendation is being made
     * @return returns recommendation if the static rules are followed or null if not
     * @see #isGettingRecommendation(UUID)
     */
    @Override
    public Optional<Recommendation> getRecommendation(UUID id) {
        logger.info("Adding SimpleCredit function was invoked for user: {}", id);
        Recommendation recommendation = null;
        if (isGettingRecommendation(id)) {
            recommendation = new Recommendation(SIMPLE_CREDIT_NAME,SIMPLE_CREDIT_ID,SIMPLE_CREDIT_DESCRIPTION);
        }
        return Optional.ofNullable(recommendation);
    }

    /**
     * Methods checks if all the static rules for the product are followed
     * @param id id of the user to whom the recommendation is being made
     * @return true is the rules are followed
     */
    public boolean isGettingRecommendation(UUID id) {
//        rule1
        IfUsedRule rule1 = new IfUsedRule(recommendationsRepository.getUse(id,"CREDIT"),false);
//        rule2
        CompareRule rule2 = new CompareRule(recommendationsRepository.getTransactionSum(id,"DEBIT","DEPOSIT"),">",recommendationsRepository.getTransactionSum(id,"DEBIT","WITHDRAW"));
//        rule3
        CompareRule rule3 = new CompareRule(recommendationsRepository.getTransactionSum(id,"DEBIT","WITHDRAW"),">",100000);

        logger.info("SimpleCredit was checked for user: {}", id);

        return rule1.isFollowed() && rule2.isFollowed() && rule3.isFollowed();
    }
}
