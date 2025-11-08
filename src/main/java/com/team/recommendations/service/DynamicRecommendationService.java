package com.team.recommendations.service;

import com.team.recommendations.model.dynamic.*;
import com.team.recommendations.model.dynamic.Comparator;
import com.team.recommendations.model.stats.RuleCounter;
import com.team.recommendations.model.recommendations.Recommendation;
import com.team.recommendations.model.rules.CompareRule;
import com.team.recommendations.model.rules.IfUsedRule;
import com.team.recommendations.repository.DynamicProductRepository;
import com.team.recommendations.repository.RecommendationsRepository;
import com.team.recommendations.service.rules.StatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DynamicRecommendationService {
    private final DynamicProductRepository dynamicProductRepository;
    private final RecommendationsRepository recommendationsRepository;
    private final StatsService statsService;

    private final Collection<String> productTypes;
    private final Collection<String> transactionTypes;
    private final Collection<String> comparators;

    private static final UUID TOP_SAVINGS_PRODUCT_ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");
    private static final String QUERY_USER_OF = "USER_OF";
    private static final String QUERY_ACTIVE_USER_OF = "ACTIVE_USER_OF";
    private static final String QUERY_TRANSACTION_SUM_COMPARE = "TRANSACTION_SUM_COMPARE";
    private static final String QUERY_TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW = "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW";

    private final Logger logger = LoggerFactory.getLogger(DynamicRecommendationService.class);

    public DynamicRecommendationService(DynamicProductRepository dynamicProductRepository, RecommendationsRepository recommendationsRepository, StatsService statsService) {
        this.dynamicProductRepository = dynamicProductRepository;
        this.recommendationsRepository = recommendationsRepository;
        this.statsService = statsService;
        this.productTypes = Arrays.stream(ProductType.values())
                .map(ProductType::getValue)
                .collect(Collectors.toList());
        this.transactionTypes = Arrays.stream(TransactionType.values())
                .map(TransactionType::getValue)
                .collect(Collectors.toList());
        this.comparators = Arrays.stream(Comparator.values())
                .map(Comparator::getSymbol)
                .collect(Collectors.toList());
    }

    @Transactional
    public Collection<Recommendation> getRecommendations(UUID userID) {
        logger.info("The method for creating recommendations was invoked");
        long start = System.nanoTime();
        Collection<Recommendation> recommendations = new ArrayList<>();
        for (DynamicProduct product : dynamicProductRepository.findAll()) {
            Collection<Boolean> ruleAbidance = new ArrayList<>();
            for (DynamicRule rule : product.getRule()) {
                switch (rule.getQuery()) {
                    case QUERY_USER_OF -> ruleAbidance.add(isUSER_OFRule(userID, rule));
                    case QUERY_ACTIVE_USER_OF -> ruleAbidance.add(isACTIVE_USER_OFRule(userID,rule));
                    case QUERY_TRANSACTION_SUM_COMPARE -> ruleAbidance.add(isTRANSACTION_SUM_COMPARERule(userID,rule));
                    case QUERY_TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW -> ruleAbidance.add(isTRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAWRule(userID,rule));
                    default -> throw new IllegalArgumentException(
                            "Unknown query type: " + rule.getQuery() + " for rule ID: " + rule.getId()
                    );
                }
            }
            if (!ruleAbidance.contains(false)) {
                countRuleApplications(product.getRule());
                Recommendation recommendation = new Recommendation(product.getProductName(), product.getProductId(), product.getProductText());
                recommendations.add(recommendation);
                logger.info("Product added for user: {}", userID);
            } else if (product.getProductId().equals(TOP_SAVINGS_PRODUCT_ID)) {
                dealWithTopSavings(userID,recommendations);
            }
        }
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        logger.info("Time elapsed for get recommendations function: {}", timeElapsed);
        return recommendations;
    }

    private void countRuleApplications(Collection<DynamicRule> rules) {
        for (DynamicRule dynamicRule : rules) {
            if (statsService.isPresentById(dynamicRule.getId())) {
                long count = statsService.getCountById(dynamicRule.getId()) + 1L;
                statsService.changeCounter(dynamicRule.getId(),count);
                logger.info("Change counter was applied");
            } else {
                RuleCounter newCounter = new RuleCounter();
                newCounter.setRuleId(dynamicRule.getId());
                newCounter.setCount(1L);
                statsService.saveNewCounter(newCounter);
            }
        }
    }

    private void dealWithTopSavings(UUID id, Collection<Recommendation> recommendations) {
        logger.info("DealWithTopSavings invoked");
        boolean alreadyExists = recommendations.stream()
                .anyMatch(r -> r.getId().equals(TOP_SAVINGS_PRODUCT_ID));

        if (alreadyExists) {
            logger.info("DealWithTopSavings: top savings was already recommended");
            return;
        }
//        rule1
        IfUsedRule rule1 = new IfUsedRule(recommendationsRepository.getUse(id,"DEBIT"),true);
//        rule2
        CompareRule rule2_1 = new CompareRule(recommendationsRepository.getTransactionSum(id,"DEBIT","DEPOSIT"),">=",50000);
        CompareRule rule2_2 = new CompareRule(recommendationsRepository.getTransactionSum(id,"SAVING","DEPOSIT"),">=",50000);
//        rule3
        CompareRule rule3 = new CompareRule(recommendationsRepository.getTransactionSum(id,"DEBIT","DEPOSIT"),">",recommendationsRepository.getTransactionSum(id,"DEBIT","WITHDRAW"));

        if (rule1.isFollowed() && (rule2_1.isFollowed() || rule2_2.isFollowed()) && rule3.isFollowed()) {
            DynamicProduct topSavings = dynamicProductRepository.findByProductId(TOP_SAVINGS_PRODUCT_ID).orElseThrow();

            Collection<DynamicRule> followedRules = new ArrayList<>();
            if (rule1.isFollowed()) followedRules.add(findRuleByPattern(topSavings, "USER_OF", List.of("DEBIT")));
            if (rule2_1.isFollowed()) followedRules.add(findRuleByPattern(topSavings, "TRANSACTION_SUM_COMPARE", List.of("DEBIT","DEPOSIT",">=","50000")));
            if (rule2_2.isFollowed()) followedRules.add(findRuleByPattern(topSavings, "TRANSACTION_SUM_COMPARE", List.of("SAVING","DEPOSIT",">=","50000")));
            if (rule3.isFollowed()) followedRules.add(findRuleByPattern(topSavings, "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW", List.of("DEBIT",">")));

            countRuleApplications(followedRules);

            Recommendation recommendation = new Recommendation(topSavings.getProductName(), topSavings.getProductId(), topSavings.getProductText());
            recommendations.add(recommendation);
            logger.info("Top Saving added through dealWith for user: {}", id);
        }
    }
    private DynamicRule findRuleByPattern(DynamicProduct product, String query, List<String> arguments) {
        return product.getRule().stream()
                .filter(r -> r.getQuery().equals(query) && r.getArguments().equals(arguments))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Rule not found: " + query + " " + arguments));
    }

    private String extractArgument(DynamicRule rule, Collection<String> arguments) {
        return rule.getArguments().stream()
                .filter(arguments::contains)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid arguments for rule " + rule.getId() +
                        ". Expected one of: " + arguments + ", got: " + rule.getArguments()));
    }

    public boolean isUSER_OFRule(UUID userID, DynamicRule rule) {
        logger.info("Checked USER_OF for {} for product {} ", userID, rule.getProduct().getProductId());
        String type = extractArgument(rule,productTypes);
        if (rule.getNegate()) {
            return !recommendationsRepository.getUse(userID, type);
        } else {
            return recommendationsRepository.getUse(userID,type);
        }
    }

    public boolean isACTIVE_USER_OFRule(UUID userID, DynamicRule rule) {
        logger.info("Checked ACTIVE_USER_OF for {} for product {} ", userID, rule.getProduct().getProductId());
        String type = extractArgument(rule,productTypes);
        if (rule.getNegate()) {
            return !recommendationsRepository.getActiveUse(userID, type);
        } else {
            return recommendationsRepository.getActiveUse(userID,type);
        }
    }
    public boolean isTRANSACTION_SUM_COMPARERule(UUID userID, DynamicRule rule) {
        logger.info("Checked TRANSACTION_SUM_COMPARE for {} for product {} ", userID, rule.getProduct().getProductId());
        String productType = extractArgument(rule,productTypes);
        String transactionType = extractArgument(rule,transactionTypes);
        String comparator = extractArgument(rule,comparators);
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
        logger.info("Checked TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW for {} for product {} ", userID, rule.getProduct().getProductId());
        String productType = extractArgument(rule,productTypes);
        String comparator = extractArgument(rule,comparators);
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
