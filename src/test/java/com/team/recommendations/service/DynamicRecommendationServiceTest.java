package com.team.recommendations.service;

import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.model.dynamic.DynamicRule;
import com.team.recommendations.model.recommendations.Recommendation;
import com.team.recommendations.repository.DynamicProductRepository;
import com.team.recommendations.repository.RecommendationsRepository;
import com.team.recommendations.service.rules.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamicRecommendationServiceTest {

    @Mock
    private DynamicProductRepository dynamicProductRepository;

    @Mock
    private RecommendationsRepository recommendationsRepository;

    @Mock
    private StatsService statsService;

    private DynamicRecommendationService dynamicRecommendationService;

    @BeforeEach
    void setUp() {
        dynamicRecommendationService = new DynamicRecommendationService(
                dynamicProductRepository,
                recommendationsRepository,
                statsService
        );
    }

    @Test
    @DisplayName("Возвращает рекомендации когда правила выполняются")
    void testGetRecommendations() {
        // given
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();

        DynamicProduct product = new DynamicProduct();
        product.setId(UUID.randomUUID());
        product.setProductId(productId);
        product.setProductName("Test Product");
        product.setProductText("Test Description");

        DynamicRule rule = new DynamicRule();
        rule.setId(ruleId);
        rule.setQuery("USER_OF");
        rule.setArguments(List.of("DEBIT"));
        rule.setNegate(false);
        rule.setProduct(product);

        product.setRule(Collections.singletonList(rule));

        when(dynamicProductRepository.findAll()).thenReturn(Collections.singletonList(product));
        when(recommendationsRepository.getUse(userId, "DEBIT")).thenReturn(true);
        when(statsService.isPresentById(ruleId)).thenReturn(true);
        when(statsService.getCountById(ruleId)).thenReturn(5L);

        // when
        Collection<Recommendation> result = dynamicRecommendationService.getRecommendations(userId);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());

        Recommendation recommendation = result.iterator().next();
        assertEquals("Test Product", recommendation.getName());
        assertEquals(productId, recommendation.getId());
        assertEquals("Test Description", recommendation.getText());

        verify(dynamicProductRepository, times(1)).findAll();
        verify(statsService, times(1)).changeCounter(ruleId, 6L);
    }

    @Test
    @DisplayName("Не возвращает рекомендации когда правила не выполняются")
    void testGetRecommendations_NoRulesMatch() {
        // given
        UUID userId = UUID.randomUUID();

        DynamicProduct product = new DynamicProduct();
        product.setProductId(UUID.randomUUID());
        product.setProductName("Test Product");

        DynamicRule rule = new DynamicRule();
        rule.setQuery("USER_OF");
        rule.setArguments(List.of("DEBIT"));
        rule.setNegate(false);
        rule.setProduct(product);

        product.setRule(Collections.singletonList(rule));

        when(dynamicProductRepository.findAll()).thenReturn(Collections.singletonList(product));
        when(recommendationsRepository.getUse(userId, "DEBIT")).thenReturn(false);

        // when
        Collection<Recommendation> result = dynamicRecommendationService.getRecommendations(userId);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(dynamicProductRepository, times(1)).findAll();
        verify(statsService, never()).changeCounter(any(), any());
    }

    @Test
    @DisplayName("Возвращает пустой список когда нет продуктов")
    void testGetRecommendations_NoProducts() {
        // given
        UUID userId = UUID.randomUUID();

        when(dynamicProductRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        Collection<Recommendation> result = dynamicRecommendationService.getRecommendations(userId);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(dynamicProductRepository, times(1)).findAll();
        verify(statsService, never()).changeCounter(any(), any());
    }
}