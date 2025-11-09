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

/**
 * Service for getting personal recommendations based on dynamic rules
 * Service gives out recommendations and also updates rule application counter which is used for statistics
 *
 * @author dlok
 * @version 1.0
 * @see DynamicProduct
 * @see DynamicRule
 */
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
    /**
     * TODO: make query enum
     */

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

    /**
     * Method for creating recommendations
     * Method checks all the products, then checks if all the rules of the product are followed
     * If so, method puts the product in the collection and updates the rule counter
     * Also invokes method for dealing with Top Saving product if current configuration has been unable to deal with it
     *
     * @param userID id of the user to whom recommendations are given
     * @return returns collection of recommendations that is later wrapped in RecommendationResponse DTO
     * @see #countRuleApplications(Collection)
     * @see #dealWithTopSavings(UUID, Collection)
     * @see #checkRule(UUID, DynamicRule)
     */
    @Transactional
    public Collection<Recommendation> getRecommendations(UUID userID) {
        logger.info("The method for creating recommendations was invoked");
        long start = System.nanoTime();
        Collection<Recommendation> recommendations = new ArrayList<>();
        for (DynamicProduct product : dynamicProductRepository.findAll()) {
            Collection<Boolean> ruleAbidance = new ArrayList<>();
            for (DynamicRule rule : product.getRule()) {
                ruleAbidance.add(checkRule(userID,rule));
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

    /**
     * Method that checks type of the rule and also checks if it is followed
     * @param userID user id
     * @param rule the rule
     * @return returns true if the rule is followed
     * @see #isUSER_OFRule(UUID, DynamicRule)
     * @see #isACTIVE_USER_OFRule(UUID, DynamicRule)
     * @see #isTRANSACTION_SUM_COMPARERule(UUID, DynamicRule)
     * @see #isTRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAWRule(UUID, DynamicRule)
     */
    private boolean checkRule(UUID userID, DynamicRule rule) {
        return switch (rule.getQuery()) {
            case QUERY_USER_OF -> isUSER_OFRule(userID, rule);
            case QUERY_ACTIVE_USER_OF -> isACTIVE_USER_OFRule(userID, rule);
            case QUERY_TRANSACTION_SUM_COMPARE -> isTRANSACTION_SUM_COMPARERule(userID, rule);
            case QUERY_TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW -> isTRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAWRule(userID, rule);
            default -> throw new IllegalArgumentException("Unknown query type: " + rule.getQuery() + " for rule ID: " + rule.getId());
        };
    }
    /**
     * Method updates rule application counter when the rules are applied
     * @param rules collection of rules to be counted
     */
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

    /**
     * Special method to deal with Top Saving product which has conflict with dynamic-rules-system
     * @param id user id
     * @param recommendations collection of recommendations that user has already received
     */
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

    /**
     * Supplementary method for associating dynamic rules with static rules
     * @param product dynamic product from repository
     * @param query desired query
     * @param arguments desired arguments
     * @return returns dynamic rule of the product which has desired query and arguments
     */
    private DynamicRule findRuleByPattern(DynamicProduct product, String query, List<String> arguments) {
        return product.getRule().stream()
                .filter(r -> r.getQuery().equals(query) && r.getArguments().equals(arguments))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Rule not found: " + query + " " + arguments));
    }

    /**
     * Method that returns specific argument from a dynamic rule
     * @param rule dynamic rule
     * @param arguments static collection of arguments one of which is desired
     * @return argument
     */
    private String extractArgument(DynamicRule rule, Collection<String> arguments) {
        return rule.getArguments().stream()
                .filter(arguments::contains)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid arguments for rule " + rule.getId() +
                        ". Expected one of: " + arguments + ", got: " + rule.getArguments()));
    }

    /**
     * Method for checking if the USER_OF rule is followed
     * @param userID user id
     * @param rule the rule
     * @return true if the USER_OF rule is followed
     */
    public boolean isUSER_OFRule(UUID userID, DynamicRule rule) {
        logger.info("Checked USER_OF for {} for product {} ", userID, rule.getProduct().getProductId());
        String type = extractArgument(rule,productTypes);
        if (rule.getNegate()) {
            return !recommendationsRepository.getUse(userID, type);
        } else {
            return recommendationsRepository.getUse(userID,type);
        }
    }

    /**
     * Method for checking if the ACTIVE_USER_OF rule is followed
     * @param userID user id
     * @param rule the rule
     * @return true if the ACTIVE_USER_OF rule is followed
     */
    public boolean isACTIVE_USER_OFRule(UUID userID, DynamicRule rule) {
        logger.info("Checked ACTIVE_USER_OF for {} for product {} ", userID, rule.getProduct().getProductId());
        String type = extractArgument(rule,productTypes);
        if (rule.getNegate()) {
            return !recommendationsRepository.getActiveUse(userID, type);
        } else {
            return recommendationsRepository.getActiveUse(userID,type);
        }
    }

    /**
     * Method for checking if the TRANSACTION_SUM_COMPARE rule is followed
     * @param userID user id
     * @param rule the rule
     * @return true if the TRANSACTION_SUM_COMPARE rule is followed
     */
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

    /**
     * Method for checking if the TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW rule is followed
     * @param userID user id
     * @param rule the rule
     * @return true if the TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW rule is followed
     */
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
