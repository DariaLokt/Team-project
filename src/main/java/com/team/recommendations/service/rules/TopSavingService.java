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
 * Service for checking if the user is to be recommended Top Saving Product based on static rules
 * Static rules are:
 * - Пользователь использует как минимум один продукт с типом DEBIT.
 * - Сумма пополнений по всем продуктам типа DEBIT больше или равна 50 000 ₽ ИЛИ Сумма пополнений по всем продуктам типа SAVING больше или равна 50 000 ₽.
 * - Сумма пополнений по всем продуктам типа DEBIT больше, чем сумма трат по всем продуктам типа DEBIT.
 *
 * @author dlok
 * @version 1.0
 */
@Service
public class TopSavingService implements RuleSetService {
    private final RecommendationsRepository recommendationsRepository;
    
    private final static String TOP_SAVING_NAME = "Top Saving";
    private final static UUID TOP_SAVING_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    private final static String TOP_SAVING_DESCRIPTION = "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!\n" +
            "\n" +
            "Преимущества «Копилки»:\n" +
            "\n" +
            "Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.\n" +
            "\n" +
            "Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.\n" +
            "\n" +
            "Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.\n" +
            "\n" +
            "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!";

    public TopSavingService(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    Logger logger = LoggerFactory.getLogger(TopSavingService.class);

    /**
     * Method to create recommendation if the static rules are followed
     * @param id id of the user to whom the recommendation is being made
     * @return returns recommendation if the static rules are followed or null if not
     * @see #isGettingRecommendation(UUID) 
     */
    @Override
    public Optional<Recommendation> getRecommendation(UUID id) {
        logger.info("Adding TopSaving function was invoked for user: {}", id);
        Recommendation recommendation = null;
        if (isGettingRecommendation(id)) {
            recommendation = new Recommendation(TOP_SAVING_NAME, TOP_SAVING_ID, TOP_SAVING_DESCRIPTION);
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
