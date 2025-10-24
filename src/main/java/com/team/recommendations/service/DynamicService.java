package com.team.recommendations.service;

import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.model.dynamic.DynamicRule;
import com.team.recommendations.repository.DynamicProductRepository;
import com.team.recommendations.repository.DynamicRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class DynamicService {
    private final DynamicProductRepository dynamicProductRepository;
    private final DynamicRuleRepository dynamicRuleRepository;

    Logger logger = LoggerFactory.getLogger(DynamicService.class);

    public DynamicService(DynamicProductRepository dynamicProductRepository, DynamicRuleRepository dynamicRuleRepository) {
        this.dynamicProductRepository = dynamicProductRepository;
        this.dynamicRuleRepository = dynamicRuleRepository;
    }

    public DynamicProduct addProduct(DynamicProduct product) {
        logger.info("The method for adding a product was invoked");
        Collection<DynamicRule> newRules = product.getRule();
        dynamicRuleRepository.saveAll(newRules);
        dynamicRuleRepository.findAll().stream()
                .filter(newRules::contains)
                .forEach(r -> r.setProduct(product));
        return dynamicProductRepository.save(product);
    }

    public Collection<DynamicProduct> getAllProducts() {
        logger.info("The method for getting all the products was invoked");
        return dynamicProductRepository.findAll();
    }

    public void deleteProduct(UUID id) {
        logger.info("The method for deleting a product was invoked");
        DynamicProduct product = dynamicProductRepository.findById(id).orElseThrow();
        dynamicRuleRepository.deleteAll(product.getRule());
        dynamicProductRepository.delete(product);
    }
}
