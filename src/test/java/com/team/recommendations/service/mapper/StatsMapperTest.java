package com.team.recommendations.service.mapper;

import com.team.recommendations.model.dynamic.DynamicRule;
import com.team.recommendations.model.stats.RuleCounter;
import com.team.recommendations.model.stats.RuleCounterDto;
import com.team.recommendations.model.stats.Stats;
import com.team.recommendations.model.stats.StatsDto;
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
class StatsMapperTest {

    @Mock
    private RuleCounterMapper ruleCounterMapper;

    private StatsMapper statsMapper;

    @BeforeEach
    void setUp() {
        statsMapper = new StatsMapper(ruleCounterMapper);
    }

    @Test
    @DisplayName("Маппит сущность статистики в дто")
    void toDto() {
        // given
        UUID ruleId1 = UUID.randomUUID();
        UUID ruleId2 = UUID.randomUUID();

        RuleCounter counter1 = new RuleCounter();
        counter1.setRuleId(ruleId1);
        counter1.setCount(10L);

        RuleCounter counter2 = new RuleCounter();
        counter2.setRuleId(ruleId2);
        counter2.setCount(20L);

        Stats entity = new Stats(Arrays.asList(counter1, counter2));

        RuleCounterDto dto1 = new RuleCounterDto();
        dto1.setRuleId(ruleId1);
        dto1.setCount(10L);

        RuleCounterDto dto2 = new RuleCounterDto();
        dto2.setRuleId(ruleId2);
        dto2.setCount(20L);

        when(ruleCounterMapper.toDtoList(any())).thenReturn(Arrays.asList(dto1, dto2));

        // when
        StatsDto result = statsMapper.toDto(entity);

        // then
        assertNotNull(result);
        assertNotNull(result.getStats());
        assertEquals(2, result.getStats().size());
        assertEquals(ruleId1, result.getStats().iterator().next().getRuleId());

        verify(ruleCounterMapper, times(1)).toDtoList(entity.getStats());
    }

    @Test
    @DisplayName("Маппит дто статистики в сущность")
    void toEntity() {
        // given
        UUID ruleId1 = UUID.randomUUID();
        UUID ruleId2 = UUID.randomUUID();

        RuleCounterDto dto1 = new RuleCounterDto();
        dto1.setRuleId(ruleId1);
        dto1.setCount(5L);

        RuleCounterDto dto2 = new RuleCounterDto();
        dto2.setRuleId(ruleId2);
        dto2.setCount(15L);

        StatsDto dto = new StatsDto(Arrays.asList(dto1, dto2));

        RuleCounter counter1 = new RuleCounter();
        counter1.setRuleId(ruleId1);
        counter1.setCount(5L);

        RuleCounter counter2 = new RuleCounter();
        counter2.setRuleId(ruleId2);
        counter2.setCount(15L);

        when(ruleCounterMapper.toEntityList(any())).thenReturn(Arrays.asList(counter1, counter2));

        // when
        Stats result = statsMapper.toEntity(dto);

        // then
        assertNotNull(result);
        assertNotNull(result.getStats());
        assertEquals(2, result.getStats().size());

        verify(ruleCounterMapper, times(1)).toEntityList(dto.getStats());
    }

    @Test
    @DisplayName("Маппит статистику с добавлением отсутствующих правил")
    void toDtoWithMissingRules() {
        // given
        UUID ruleId1 = UUID.randomUUID();
        UUID ruleId2 = UUID.randomUUID();
        UUID ruleId3 = UUID.randomUUID();

        RuleCounter counter1 = new RuleCounter();
        counter1.setRuleId(ruleId1);
        counter1.setCount(10L);

        RuleCounter counter2 = new RuleCounter();
        counter2.setRuleId(ruleId2);
        counter2.setCount(20L);

        Stats stats = new Stats(Arrays.asList(counter1, counter2));

        DynamicRule rule1 = new DynamicRule();
        rule1.setId(ruleId1);

        DynamicRule rule2 = new DynamicRule();
        rule2.setId(ruleId2);

        DynamicRule rule3 = new DynamicRule();
        rule3.setId(ruleId3);

        List<DynamicRule> allRules = Arrays.asList(rule1, rule2, rule3);

        // when
        StatsDto result = statsMapper.toDtoWithMissingRules(stats, allRules);

        // then
        assertNotNull(result);
        assertNotNull(result.getStats());
        assertEquals(3, result.getStats().size());

        Map<UUID, Long> resultMap = new HashMap<>();
        result.getStats().forEach(dto -> resultMap.put(dto.getRuleId(), dto.getCount()));

        assertEquals(10L, resultMap.get(ruleId1));
        assertEquals(20L, resultMap.get(ruleId2));
        assertEquals(0L, resultMap.get(ruleId3));
    }

    @Test
    @DisplayName("Маппит статистику когда stats null")
    void toDtoWithMissingRules_WhenStatsIsNull() {
        // given
        UUID ruleId1 = UUID.randomUUID();
        UUID ruleId2 = UUID.randomUUID();

        DynamicRule rule1 = new DynamicRule();
        rule1.setId(ruleId1);

        DynamicRule rule2 = new DynamicRule();
        rule2.setId(ruleId2);

        List<DynamicRule> allRules = Arrays.asList(rule1, rule2);

        // when
        StatsDto result = statsMapper.toDtoWithMissingRules(null, allRules);

        // then
        assertNotNull(result);
        assertNotNull(result.getStats());
        assertEquals(2, result.getStats().size());

        result.getStats().forEach(dto -> assertEquals(0L, dto.getCount()));
    }

    @Test
    @DisplayName("Маппит статистику когда allRules пустой")
    void toDtoWithMissingRules_WhenAllRulesIsEmpty() {
        // given
        UUID ruleId1 = UUID.randomUUID();

        RuleCounter counter1 = new RuleCounter();
        counter1.setRuleId(ruleId1);
        counter1.setCount(10L);

        Stats stats = new Stats(Collections.singletonList(counter1));

        // when
        StatsDto result = statsMapper.toDtoWithMissingRules(stats, Collections.emptyList());

        // then
        assertNotNull(result);
        assertNotNull(result.getStats());
        assertEquals(1, result.getStats().size());
        assertEquals(ruleId1, result.getStats().iterator().next().getRuleId());
        assertEquals(10L, result.getStats().iterator().next().getCount());
    }

    @Test
    @DisplayName("Возвращает null когда сущность null")
    void toDto_ShouldReturnNull_WhenEntityIsNull() {
        // when
        StatsDto result = statsMapper.toDto(null);

        // then
        assertNull(result);
        verifyNoInteractions(ruleCounterMapper);
    }

    @Test
    @DisplayName("Возвращает null когда дто null")
    void toEntity_ShouldReturnNull_WhenDtoIsNull() {
        // when
        Stats result = statsMapper.toEntity(null);

        // then
        assertNull(result);
        verifyNoInteractions(ruleCounterMapper);
    }

    @Test
    @DisplayName("Корректно обрабатывает перекрывающиеся правила")
    void toDtoWithMissingRules_WithOverlappingRules() {
        // given
        UUID ruleId1 = UUID.randomUUID();
        UUID ruleId2 = UUID.randomUUID();
        UUID ruleId3 = UUID.randomUUID();

        RuleCounter counter1 = new RuleCounter();
        counter1.setRuleId(ruleId1);
        counter1.setCount(10L);

        RuleCounter counter2 = new RuleCounter();
        counter2.setRuleId(ruleId2);
        counter2.setCount(20L);

        Stats stats = new Stats(Arrays.asList(counter1, counter2));

        DynamicRule rule1 = new DynamicRule();
        rule1.setId(ruleId1);

        DynamicRule rule2 = new DynamicRule();
        rule2.setId(ruleId2);

        DynamicRule rule3 = new DynamicRule();
        rule3.setId(ruleId3);

        List<DynamicRule> allRules = Arrays.asList(rule1, rule2, rule3);

        // when
        StatsDto result = statsMapper.toDtoWithMissingRules(stats, allRules);

        // then
        assertNotNull(result);
        assertEquals(3, result.getStats().size());

        Map<UUID, Long> resultMap = new HashMap<>();
        result.getStats().forEach(dto -> resultMap.put(dto.getRuleId(), dto.getCount()));

        assertEquals(10L, resultMap.get(ruleId1));
        assertEquals(20L, resultMap.get(ruleId2));
        assertEquals(0L, resultMap.get(ruleId3));
    }
}