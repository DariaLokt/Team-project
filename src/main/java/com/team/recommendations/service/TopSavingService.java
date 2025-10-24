package com.team.recommendations.service;

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
public class TopSavingService implements RuleSetService {
    private final RecommendationsRepository recommendationsRepository;

    public TopSavingService(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    Logger logger = LoggerFactory.getLogger(TopSavingService.class);

    @Override
    public Optional<Recommendation> getRecommendation(UUID id) {
        logger.info("Adding TopSaving function was invoked for user: {}", id);
        Recommendation recommendation = null;
        if (isGettingRecommendation(id)) {
            recommendation = new Recommendation("Top Saving", UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
                    "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!\n" +
                            "\n" +
                            "Преимущества «Копилки»:\n" +
                            "\n" +
                            "Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.\n" +
                            "\n" +
                            "Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.\n" +
                            "\n" +
                            "Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.\n" +
                            "\n" +
                            "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!");
        }
        return Optional.ofNullable(recommendation);
    }

    public boolean isGettingRecommendation(UUID id) {
//        rule1
        IfUsedRule rule1 = new IfUsedRule(recommendationsRepository.getUse(id,"DEBIT"),true);
//        rule2
        CompareRule rule2_1 = new CompareRule(recommendationsRepository.getTransactionSum(id,"DEBIT","DEPOSIT"),">=",50000);
        CompareRule rule2_2 = new CompareRule(recommendationsRepository.getTransactionSum(id,"SAVING","DEPOSIT"),">=",50000);
//        rule3
        CompareRule rule3 = new CompareRule(recommendationsRepository.getTransactionSum(id,"DEBIT","DEPOSIT"),">",recommendationsRepository.getTransactionSum(id,"DEBIT","WITHDRAW"));

        logger.info("TopSaving was checked for user: {}", id);

        return rule1.isFollowed() && (rule2_1.isFollowed() || rule2_2.isFollowed()) && rule3.isFollowed();
    }
}
