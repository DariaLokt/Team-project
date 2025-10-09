package com.team.recommendations.service;

import com.team.recommendations.model.recommendations.Recommendation;
import com.team.recommendations.model.rules.CompareRule;
import com.team.recommendations.model.rules.IfUsedRule;
import com.team.recommendations.repository.RecommendationsRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SimpleCreditService implements RuleSetService {
    private final RecommendationsRepository recommendationsRepository;

    public SimpleCreditService(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    @Override
    public Optional<Recommendation> getRecommendation(UUID id) {
        Recommendation recommendation = null;
        if (isGettingRecommendation(id)) {
            recommendation = new Recommendation("Простой кредит", UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"),
                    "Откройте мир выгодных кредитов с нами!\n" +
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
                            "Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!");
        }
        return Optional.ofNullable(recommendation);
    }

    public boolean isGettingRecommendation(UUID id) {
//        rule1
        IfUsedRule rule1 = new IfUsedRule(recommendationsRepository.getUse(id,"CREDIT"),false);
//        rule2
        CompareRule rule2 = new CompareRule(recommendationsRepository.getTransactionSum(id,"DEBIT","DEPOSIT"),">",recommendationsRepository.getTransactionSum(id,"DEBIT","WITHDRAW"));
//        rule3
        CompareRule rule3 = new CompareRule(recommendationsRepository.getTransactionSum(id,"DEBIT","WITHDRAW"),">",100000);

        return rule1.isFollowed() && rule2.isFollowed() && rule3.isFollowed();
    }
}
