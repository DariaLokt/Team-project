package com.team.recommendations.service.mapper;

import com.team.recommendations.model.stats.RuleCounter;
import com.team.recommendations.model.stats.RuleCounterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RuleCounterMapperTest {

    private RuleCounterMapper ruleCounterMapper;

    @BeforeEach
    void setUp() {
        ruleCounterMapper = new RuleCounterMapper();
    }

    @Test
    @DisplayName("Маппит сущность счетчика в дто")
    void toDto() {
        // given
        UUID ruleId = UUID.randomUUID();
        Long count = 42L;

        RuleCounter entity = new RuleCounter();
        entity.setRuleId(ruleId);
        entity.setCount(count);

        // when
        RuleCounterDto result = ruleCounterMapper.toDto(entity);

        // then
        assertNotNull(result);
        assertEquals(ruleId, result.getRuleId());
        assertEquals(count, result.getCount());
    }

    @Test
    @DisplayName("Маппит дто счетчика в сущность")
    void toEntity() {
        // given
        UUID ruleId = UUID.randomUUID();
        Long count = 15L;

        RuleCounterDto dto = new RuleCounterDto();
        dto.setRuleId(ruleId);
        dto.setCount(count);

        // when
        RuleCounter result = ruleCounterMapper.toEntity(dto);

        // then
        assertNotNull(result);
        assertEquals(ruleId, result.getRuleId());
        assertEquals(count, result.getCount());
    }

    @Test
    @DisplayName("Маппит сущности счетчиков в лист дто")
    void toDtoList() {
        // given
        UUID ruleId1 = UUID.randomUUID();
        UUID ruleId2 = UUID.randomUUID();

        RuleCounter entity1 = new RuleCounter();
        entity1.setRuleId(ruleId1);
        entity1.setCount(10L);

        RuleCounter entity2 = new RuleCounter();
        entity2.setRuleId(ruleId2);
        entity2.setCount(20L);

        List<RuleCounter> entities = Arrays.asList(entity1, entity2);

        // when
        List<RuleCounterDto> result = ruleCounterMapper.toDtoList(entities);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ruleId1, result.get(0).getRuleId());
        assertEquals(ruleId2, result.get(1).getRuleId());
        assertEquals(10L, result.get(0).getCount());
        assertEquals(20L, result.get(1).getCount());
    }

    @Test
    @DisplayName("Маппит дто счетчиков в лист сущностей")
    void toEntityList() {
        // given
        UUID ruleId1 = UUID.randomUUID();
        UUID ruleId2 = UUID.randomUUID();

        RuleCounterDto dto1 = new RuleCounterDto();
        dto1.setRuleId(ruleId1);
        dto1.setCount(5L);

        RuleCounterDto dto2 = new RuleCounterDto();
        dto2.setRuleId(ruleId2);
        dto2.setCount(25L);

        List<RuleCounterDto> dto = Arrays.asList(dto1, dto2);

        // when
        List<RuleCounter> result = ruleCounterMapper.toEntityList(dto);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ruleId1, result.get(0).getRuleId());
        assertEquals(ruleId2, result.get(1).getRuleId());
        assertEquals(5L, result.get(0).getCount());
        assertEquals(25L, result.get(1).getCount());
    }

    @Test
    @DisplayName("Возвращает null когда сущность null")
    void toDto_ShouldReturnNull_WhenEntityIsNull() {
        // when
        RuleCounterDto result = ruleCounterMapper.toDto(null);

        // then
        assertNull(result);
    }

    @Test
    @DisplayName("Возвращает null когда дто null")
    void toEntity_ShouldReturnNull_WhenDtoIsNull() {
        // when
        RuleCounter result = ruleCounterMapper.toEntity(null);

        // then
        assertNull(result);
    }

    @Test
    @DisplayName("Возвращает пустой лист когда лист сущностей null")
    void toDtoList_ShouldReturnEmptyList_WhenEntitiesIsNull() {
        // when
        List<RuleCounterDto> result = ruleCounterMapper.toDtoList(null);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Возвращает пустой лист когда лист дто null")
    void toEntityList_ShouldReturnEmptyList_WhenDtoIsNull() {
        // when
        List<RuleCounter> result = ruleCounterMapper.toEntityList(null);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Корректно работает с пустым листом сущностей")
    void toDtoList_ShouldReturnEmptyList_WhenEntitiesIsEmpty() {
        // when
        List<RuleCounterDto> result = ruleCounterMapper.toDtoList(Collections.emptyList());

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Корректно работает с пустым листом дто")
    void toEntityList_ShouldReturnEmptyList_WhenDtoIsEmpty() {
        // when
        List<RuleCounter> result = ruleCounterMapper.toEntityList(Collections.emptyList());

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Корректно маппит счетчик с нулевым значением")
    void toDto_ShouldWorkWithZeroCount() {
        // given
        UUID ruleId = UUID.randomUUID();

        RuleCounter entity = new RuleCounter();
        entity.setRuleId(ruleId);
        entity.setCount(0L);

        // when
        RuleCounterDto result = ruleCounterMapper.toDto(entity);

        // then
        assertNotNull(result);
        assertEquals(ruleId, result.getRuleId());
        assertEquals(0L, result.getCount());
    }

    @Test
    @DisplayName("Корректно маппит счетчик с большим значением")
    void toDto_ShouldWorkWithLargeCount() {
        // given
        UUID ruleId = UUID.randomUUID();
        Long largeCount = 1_000_000L;

        RuleCounter entity = new RuleCounter();
        entity.setRuleId(ruleId);
        entity.setCount(largeCount);

        // when
        RuleCounterDto result = ruleCounterMapper.toDto(entity);

        // then
        assertNotNull(result);
        assertEquals(ruleId, result.getRuleId());
        assertEquals(largeCount, result.getCount());
    }
}