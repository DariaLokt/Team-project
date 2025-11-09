package com.team.recommendations.service.mapper;

import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.model.dynamic.DynamicRule;
import com.team.recommendations.model.dynamic.DynamicRuleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DynamicRuleMapperTest {

    private DynamicRuleMapper dynamicRuleMapper;

    @BeforeEach
    void setUp() {
        dynamicRuleMapper = new DynamicRuleMapper();
    }

    @Test
    @DisplayName("Маппит сущность правила в дто")
    void toDto() {
        // given
        UUID ruleId = UUID.randomUUID();

        DynamicRule entity = new DynamicRule();
        entity.setId(ruleId);
        entity.setQuery("USER_OF");
        entity.setArguments(List.of("DEBIT", "CREDIT"));
        entity.setNegate(true);

        // when
        DynamicRuleDto result = dynamicRuleMapper.toDto(entity);

        // then
        assertNotNull(result);
        assertEquals("USER_OF", result.getQuery());
        assertEquals(List.of("DEBIT", "CREDIT"), result.getArguments());
        assertTrue(result.getNegate());
    }

    @Test
    @DisplayName("Маппит дто правила в сущность без продукта")
    void toEntity() {
        // given
        DynamicRuleDto dto = new DynamicRuleDto();
        dto.setQuery("TRANSACTION_SUM_COMPARE");
        dto.setArguments(List.of("SAVING", "DEPOSIT", ">", "1000"));
        dto.setNegate(false);

        // when
        DynamicRule result = dynamicRuleMapper.toEntity(dto);

        // then
        assertNotNull(result);
        assertEquals("TRANSACTION_SUM_COMPARE", result.getQuery());
        assertEquals(List.of("SAVING", "DEPOSIT", ">", "1000"), result.getArguments());
        assertFalse(result.getNegate());
        assertNull(result.getProduct());
    }

    @Test
    @DisplayName("Маппит дто правила в сущность с продуктом")
    void toEntity_WithProduct() {
        // given
        DynamicRuleDto dto = new DynamicRuleDto();
        dto.setQuery("ACTIVE_USER_OF");
        dto.setArguments(List.of("INVEST"));
        dto.setNegate(true);

        DynamicProduct product = new DynamicProduct();
        product.setId(UUID.randomUUID());
        product.setProductName("Test Product");

        // when
        DynamicRule result = dynamicRuleMapper.toEntity(dto, product);

        // then
        assertNotNull(result);
        assertEquals("ACTIVE_USER_OF", result.getQuery());
        assertEquals(List.of("INVEST"), result.getArguments());
        assertTrue(result.getNegate());
        assertEquals(product, result.getProduct());
    }

    @Test
    @DisplayName("Маппит сущности правил в лист дто")
    void toDtoList() {
        // given
        DynamicRule entity1 = new DynamicRule();
        entity1.setQuery("USER_OF");
        entity1.setArguments(List.of("DEBIT"));
        entity1.setNegate(false);

        DynamicRule entity2 = new DynamicRule();
        entity2.setQuery("TRANSACTION_SUM_COMPARE");
        entity2.setArguments(List.of("SAVING", "DEPOSIT", ">", "50000"));
        entity2.setNegate(true);

        List<DynamicRule> entities = Arrays.asList(entity1, entity2);

        // when
        List<DynamicRuleDto> result = dynamicRuleMapper.toDtoList(entities);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("USER_OF", result.get(0).getQuery());
        assertEquals("TRANSACTION_SUM_COMPARE", result.get(1).getQuery());
        assertFalse(result.get(0).getNegate());
        assertTrue(result.get(1).getNegate());
    }

    @Test
    @DisplayName("Маппит дто правил в лист сущностей без продукта")
    void toEntityList() {
        // given
        DynamicRuleDto dto1 = new DynamicRuleDto();
        dto1.setQuery("USER_OF");
        dto1.setArguments(List.of("DEBIT"));
        dto1.setNegate(false);

        DynamicRuleDto dto2 = new DynamicRuleDto();
        dto2.setQuery("ACTIVE_USER_OF");
        dto2.setArguments(List.of("CREDIT"));
        dto2.setNegate(true);

        List<DynamicRuleDto> dto = Arrays.asList(dto1, dto2);

        // when
        List<DynamicRule> result = dynamicRuleMapper.toEntityList(dto);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("USER_OF", result.get(0).getQuery());
        assertEquals("ACTIVE_USER_OF", result.get(1).getQuery());
        assertFalse(result.get(0).getNegate());
        assertTrue(result.get(1).getNegate());
        assertNull(result.get(0).getProduct());
        assertNull(result.get(1).getProduct());
    }

    @Test
    @DisplayName("Маппит дто правил в лист сущностей с продуктом")
    void toEntityList_WithProduct() {
        // given
        DynamicRuleDto dto1 = new DynamicRuleDto();
        dto1.setQuery("USER_OF");
        dto1.setArguments(List.of("DEBIT"));
        dto1.setNegate(false);

        DynamicRuleDto dto2 = new DynamicRuleDto();
        dto2.setQuery("TRANSACTION_SUM_COMPARE");
        dto2.setArguments(List.of("SAVING", "DEPOSIT", ">", "1000"));
        dto2.setNegate(false);

        List<DynamicRuleDto> dto = Arrays.asList(dto1, dto2);

        DynamicProduct product = new DynamicProduct();
        product.setId(UUID.randomUUID());
        product.setProductName("Test Product");

        // when
        List<DynamicRule> result = dynamicRuleMapper.toEntityList(dto, product);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("USER_OF", result.get(0).getQuery());
        assertEquals("TRANSACTION_SUM_COMPARE", result.get(1).getQuery());
        assertEquals(product, result.get(0).getProduct());
        assertEquals(product, result.get(1).getProduct());
    }

    @Test
    @DisplayName("Возвращает null когда сущность null")
    void toDto_ShouldReturnNull_WhenEntityIsNull() {
        // when
        DynamicRuleDto result = dynamicRuleMapper.toDto(null);

        // then
        assertNull(result);
    }

    @Test
    @DisplayName("Возвращает null когда дто null")
    void toEntity_ShouldReturnNull_WhenDtoIsNull() {
        // when
        DynamicRule result = dynamicRuleMapper.toEntity(null);

        // then
        assertNull(result);
    }

    @Test
    @DisplayName("Возвращает null когда дто и продукт null")
    void toEntity_ShouldReturnNull_WhenDtoAndProductAreNull() {
        // when
        DynamicRule result = dynamicRuleMapper.toEntity(null, null);

        // then
        assertNull(result);
    }

    @Test
    @DisplayName("Возвращает пустой лист когда лист сущностей null")
    void toDtoList_ShouldReturnEmptyList_WhenEntitiesIsNull() {
        // when
        List<DynamicRuleDto> result = dynamicRuleMapper.toDtoList(null);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Возвращает пустой лист когда лист дто null")
    void toEntityList_ShouldReturnEmptyList_WhenDtoIsNull() {
        // when
        List<DynamicRule> result = dynamicRuleMapper.toEntityList(null);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Возвращает пустой лист когда лист дто и продукт null")
    void toEntityList_ShouldReturnEmptyList_WhenDtoAndProductAreNull() {
        // when
        List<DynamicRule> result = dynamicRuleMapper.toEntityList(null, null);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}