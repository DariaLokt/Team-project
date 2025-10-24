package com.team.recommendations.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.model.dynamic.DynamicRule;
import com.team.recommendations.model.recommendations.Recommendation;
import com.team.recommendations.model.rules.CompareRule;
import com.team.recommendations.model.rules.IfUsedRule;
import com.team.recommendations.repository.DynamicProductRepository;
import com.team.recommendations.repository.RecommendationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DynamicRecommendationService {
    private final DynamicProductRepository dynamicProductRepository;
    private final RecommendationsRepository recommendationsRepository;
    private final Collection<String> argsProdType;
    private final Collection<String> argsTransType;
    private final Collection<String> argsComparator;

    Logger logger = LoggerFactory.getLogger(DynamicRecommendationService.class);

    public DynamicRecommendationService(DynamicProductRepository dynamicProductRepository, RecommendationsRepository recommendationsRepository) {
        this.dynamicProductRepository = dynamicProductRepository;
        this.recommendationsRepository = recommendationsRepository;
        this.argsProdType = List.of("DEBIT","CREDIT","INVEST","SAVING");
        this.argsTransType = List.of("WITHDRAW","DEPOSIT");
        this.argsComparator = List.of(">","<","=",">=","<=");
    }

    Cache<UUID, Collection<Recommendation>> cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @Cacheable("recommendations")
    public Collection<Recommendation> getRecommendations(UUID userID) {
        logger.info("The method for creating recommendations was invoked");
        long start = System.nanoTime();
        Collection<Recommendation> recommendations = new ArrayList<>();
        for (DynamicProduct product : dynamicProductRepository.findAll()) {
            Collection<Boolean> ruleAbidance = new ArrayList<>();
            for (DynamicRule rule : product.getRule()) {
                switch (rule.getQuery()) {
                    case "USER_OF" -> ruleAbidance.add(isUSER_OFRule(userID, rule));
                    case "ACTIVE_USER_OF" -> ruleAbidance.add(isACTIVE_USER_OFRule(userID,rule));
                    case "TRANSACTION_SUM_COMPARE" -> ruleAbidance.add(isTRANSACTION_SUM_COMPARERule(userID,rule));
                    case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> ruleAbidance.add(isTRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAWRule(userID,rule));
                    default -> throw new RuntimeException("No such query");
                }
            }
            if (!ruleAbidance.contains(false)) {
                Recommendation recommendation = new Recommendation(product.getProduct_name(), product.getProduct_id(), product.getProduct_text());
                recommendations.add(recommendation);
                logger.info("Product added for user: {}", userID);
            } else if (String.valueOf(product.getProduct_id()).equals("59efc529-2fff-41af-baff-90ccd7402925")) {
                if (dealWithTopSavings(userID)) {
                    Recommendation recommendation = new Recommendation(product.getProduct_name(), product.getProduct_id(), product.getProduct_text());
                    recommendations.add(recommendation);
                    logger.info("Top Saving added through dealWith for user: {}", userID);
                }
            }
        }
        cache.put(userID,recommendations);
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        logger.info("Time elapsed for get recommendations function: {}", timeElapsed);
        return recommendations;
    }

    private boolean dealWithTopSavings(UUID id) {
//        rule1
        IfUsedRule rule1 = new IfUsedRule(recommendationsRepository.getUse(id,"DEBIT"),true);
//        rule2
        CompareRule rule2_1 = new CompareRule(recommendationsRepository.getTransactionSum(id,"DEBIT","DEPOSIT"),">=",50000);
        CompareRule rule2_2 = new CompareRule(recommendationsRepository.getTransactionSum(id,"SAVING","DEPOSIT"),">=",50000);
//        rule3
        CompareRule rule3 = new CompareRule(recommendationsRepository.getTransactionSum(id,"DEBIT","DEPOSIT"),">",recommendationsRepository.getTransactionSum(id,"DEBIT","WITHDRAW"));

        logger.info("DealWithTopSavings invoked");
        return rule1.isFollowed() && (rule2_1.isFollowed() || rule2_2.isFollowed()) && rule3.isFollowed();
    }

    public boolean isUSER_OFRule(UUID userID, DynamicRule rule) {
        logger.info("Checked USER_OF for {} for product {} ", userID, rule.getProduct().getProduct_id());
        String type = rule.getArguments().stream().filter(argsProdType::contains).findAny().orElseThrow();
        if (rule.getNegate()) {
            return !recommendationsRepository.getUse(userID, type);
        } else {
            return recommendationsRepository.getUse(userID,type);
        }
    }

    public boolean isACTIVE_USER_OFRule(UUID userID, DynamicRule rule) {
        logger.info("Checked ACTIVE_USER_OF for {} for product {} ", userID, rule.getProduct().getProduct_id());
        String type = rule.getArguments().stream().filter(argsProdType::contains).findAny().orElseThrow();
        if (rule.getNegate()) {
            return !recommendationsRepository.getActiveUse(userID, type);
        } else {
            return recommendationsRepository.getActiveUse(userID,type);
        }
    }
    public boolean isTRANSACTION_SUM_COMPARERule(UUID userID, DynamicRule rule) {
        logger.info("Checked TRANSACTION_SUM_COMPARE for {} for product {} ", userID, rule.getProduct().getProduct_id());
        String productType = rule.getArguments().stream().filter(argsProdType::contains).findAny().orElseThrow();
        String transactionType = rule.getArguments().stream().filter(argsTransType::contains).findAny().orElseThrow();
        String comparator = rule.getArguments().stream().filter(argsComparator::contains).findAny().orElseThrow();
        String number = rule.getArguments().stream().filter(a -> a.matches("\\d+")).findAny().orElseThrow();
        int value = recommendationsRepository.getTransactionSum(userID,productType,transactionType);
        CompareRule compareRule = new CompareRule(value,comparator,Integer.parseInt(number));
        if (rule.getNegate()) {
            return !compareRule.isFollowed();
        } else {
            return compareRule.isFollowed();
        }
    }
    public boolean isTRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAWRule(UUID userID, DynamicRule rule) {
        logger.info("Checked TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW for {} for product {} ", userID, rule.getProduct().getProduct_id());
        String productType = rule.getArguments().stream().filter(argsProdType::contains).findAny().orElseThrow();
        String comparator = rule.getArguments().stream().filter(argsComparator::contains).findAny().orElseThrow();
        int valueDEPOSIT = recommendationsRepository.getTransactionSum(userID,productType,"DEPOSIT");
        int valueWITHDRAW = recommendationsRepository.getTransactionSum(userID,productType,"WITHDRAW");
        CompareRule compareRule = new CompareRule(valueDEPOSIT,comparator,valueWITHDRAW);
        if (rule.getNegate()) {
            return !compareRule.isFollowed();
        } else {
            return compareRule.isFollowed();
        }
    }
}
