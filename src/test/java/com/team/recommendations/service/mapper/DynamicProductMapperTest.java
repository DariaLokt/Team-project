package com.team.recommendations.service.mapper;

import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.model.dynamic.DynamicProductDto;
import com.team.recommendations.model.dynamic.DynamicRule;
import com.team.recommendations.model.dynamic.DynamicRuleDto;
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
class DynamicProductMapperTest {

    @Mock
    private DynamicRuleMapper dynamicRuleMapper;

    private DynamicProductMapper dynamicProductMapper;

    @BeforeEach
    void setUp() {
        dynamicProductMapper = new DynamicProductMapper(dynamicRuleMapper);
    }

    @Test
    @DisplayName("Маппит сущность в дто")
    void toDto() {
        // given
        UUID productId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();

        DynamicRule rule = new DynamicRule();
        rule.setId(ruleId);
        rule.setQuery("USER_OF");
        rule.setArguments(List.of("DEBIT"));
        rule.setNegate(false);

        DynamicProduct entity = new DynamicProduct();
        entity.setId(productId);
        entity.setProductName("Test Product");
        entity.setProductId(UUID.randomUUID());
        entity.setProductText("Test Description");
        entity.setRule(Collections.singletonList(rule));

        DynamicRuleDto ruleDto = new DynamicRuleDto();
        ruleDto.setQuery("USER_OF");
        ruleDto.setArguments(List.of("DEBIT"));
        ruleDto.setNegate(false);

        when(dynamicRuleMapper.toDtoList(any())).thenReturn(Collections.singletonList(ruleDto));

        // when
        DynamicProductDto result = dynamicProductMapper.toDto(entity);

        // then
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Test Product", result.getProductName());
        assertEquals("Test Description", result.getProductText());
        assertNotNull(result.getRule());
        assertEquals(1, result.getRule().size());
        assertEquals("USER_OF", result.getRule().iterator().next().getQuery());

        verify(dynamicRuleMapper, times(1)).toDtoList(entity.getRule());
    }

    @Test
    @DisplayName("Маппит дто в сущность")
    void toEntity() {
        // given
        UUID productId = UUID.randomUUID();

        DynamicRuleDto ruleDto = new DynamicRuleDto();
        ruleDto.setQuery("USER_OF");
        ruleDto.setArguments(List.of("DEBIT"));
        ruleDto.setNegate(false);

        DynamicProductDto dto = new DynamicProductDto();
        dto.setId(productId);
        dto.setProductName("Test Product");
        dto.setProductId(UUID.randomUUID());
        dto.setProductText("Test Description");
        dto.setRule(Collections.singletonList(ruleDto));

        DynamicRule rule = new DynamicRule();
        rule.setQuery("USER_OF");
        rule.setArguments(List.of("DEBIT"));
        rule.setNegate(false);

        when(dynamicRuleMapper.toEntityList(any(), any())).thenReturn(Collections.singletonList(rule));

        // when
        DynamicProduct result = dynamicProductMapper.toEntity(dto);

        // then
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Test Product", result.getProductName());
        assertEquals("Test Description", result.getProductText());
        assertNotNull(result.getRule());
        assertEquals(1, result.getRule().size());
        assertEquals("USER_OF", result.getRule().iterator().next().getQuery());

        verify(dynamicRuleMapper, times(1)).toEntityList(dto.getRule(), result);
    }

    @Test
    @DisplayName("Маппит сущности в лист дто")
    void toDtoList() {
        // given
        DynamicProduct entity1 = new DynamicProduct();
        entity1.setId(UUID.randomUUID());
        entity1.setProductName("Product 1");
        entity1.setRule(Collections.emptyList());

        DynamicProduct entity2 = new DynamicProduct();
        entity2.setId(UUID.randomUUID());
        entity2.setProductName("Product 2");
        entity2.setRule(Collections.emptyList());

        List<DynamicProduct> entities = Arrays.asList(entity1, entity2);

        when(dynamicRuleMapper.toDtoList(any())).thenReturn(Collections.emptyList());

        // when
        List<DynamicProductDto> result = dynamicProductMapper.toDtoList(entities);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getProductName());
        assertEquals("Product 2", result.get(1).getProductName());

        verify(dynamicRuleMapper, times(2)).toDtoList(Collections.emptyList());
    }

    @Test
    @DisplayName("Маппит дто в лист сущностей")
    void toEntityList() {
        // given
        DynamicProductDto dto1 = new DynamicProductDto();
        dto1.setId(UUID.randomUUID());
        dto1.setProductName("Product 1");
        dto1.setRule(Collections.emptyList());

        DynamicProductDto dto2 = new DynamicProductDto();
        dto2.setId(UUID.randomUUID());
        dto2.setProductName("Product 2");
        dto2.setRule(Collections.emptyList());

        List<DynamicProductDto> dtos = Arrays.asList(dto1, dto2);

        when(dynamicRuleMapper.toEntityList(any(Collection.class), any(DynamicProduct.class)))
                .thenReturn(Collections.emptyList());

        // when
        List<DynamicProduct> result = dynamicProductMapper.toEntityList(dtos);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getProductName());
        assertEquals("Product 2", result.get(1).getProductName());

        verify(dynamicRuleMapper, times(2)).toEntityList(any(Collection.class), any(DynamicProduct.class));
    }

    @Test
    @DisplayName("Возвращает null когда дто null")
    void toEntity_ShouldReturnNull_WhenDtoIsNull() {
        // when
        DynamicProduct result = dynamicProductMapper.toEntity(null);

        // then
        assertNull(result);
        verifyNoInteractions(dynamicRuleMapper);
    }

    @Test
    @DisplayName("Возвращает null когда лист сущностей null")
    void toDtoList_ShouldReturnEmptyList_WhenEntitiesIsNull() {
        // when
        List<DynamicProductDto> result = dynamicProductMapper.toDtoList(null);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(dynamicRuleMapper);
    }

    @Test
    @DisplayName("Возвращает null когда лист дто null")
    void toEntityList_ShouldReturnEmptyList_WhenDtoIsNull() {
        // when
        List<DynamicProduct> result = dynamicProductMapper.toEntityList(null);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(dynamicRuleMapper);
    }
}