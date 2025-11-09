package com.team.recommendations.service.rules;

import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.model.dynamic.DynamicProductDto;
import com.team.recommendations.model.dynamic.DynamicRuleDto;
import com.team.recommendations.repository.DynamicProductRepository;
import com.team.recommendations.repository.DynamicRuleRepository;
import com.team.recommendations.service.mapper.DynamicProductMapper;
import com.team.recommendations.service.mapper.DynamicRuleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for adding, deleting and getting dynamic products
 * Service also maps Dynamic Products to DTO
 * @see DynamicProduct
 * @see DynamicProductMapper
 * @see DynamicProductDto
 *
 * @author dlok
 * @version 1.0
 */
@Service
public class DynamicService {
    private final DynamicProductRepository dynamicProductRepository;
    private final DynamicRuleRepository dynamicRuleRepository;
    private final DynamicProductMapper dynamicProductMapper;
    private final DynamicRuleMapper dynamicRuleMapper;

    private final Logger logger = LoggerFactory.getLogger(DynamicService.class);

    public DynamicService(DynamicProductRepository dynamicProductRepository, DynamicRuleRepository dynamicRuleRepository, DynamicProductMapper dynamicProductMapper, DynamicRuleMapper dynamicRuleMapper) {
        this.dynamicProductRepository = dynamicProductRepository;
        this.dynamicRuleRepository = dynamicRuleRepository;
        this.dynamicProductMapper = dynamicProductMapper;
        this.dynamicRuleMapper = dynamicRuleMapper;
    }

    public DynamicProductDto addProduct(DynamicProductDto productDto) {
        logger.info("The method for adding a product was invoked");
        DynamicProduct productEntity = dynamicProductMapper.toEntity(productDto);
        Collection<DynamicRuleDto> newRulesDto = productDto.getRule();
        newRulesDto.stream()
                        .map(dynamicRuleMapper::toEntity)
                                .forEach((rule) -> {
                                    dynamicRuleRepository.save(rule);
                                    rule.setProduct(productEntity);
                                });
        dynamicProductRepository.save(productEntity);
        return productDto;
    }

    public Collection<DynamicProductDto> getAllProducts() {
        logger.info("The method for getting all the products was invoked");
        return dynamicProductRepository.findAll().stream()
                .map(dynamicProductMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteProduct(UUID id) {
        logger.info("The method for deleting a product was invoked");
        DynamicProduct product = dynamicProductRepository.findById(id).orElseThrow();
        dynamicRuleRepository.deleteAll(product.getRule());
        dynamicProductRepository.delete(product);
    }
}
