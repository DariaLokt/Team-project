package com.team.recommendations.service.rules;

import com.team.recommendations.model.stats.RuleCounter;
import com.team.recommendations.model.stats.Stats;
import com.team.recommendations.model.stats.StatsDto;
import com.team.recommendations.repository.DynamicRuleRepository;
import com.team.recommendations.repository.RuleCounterRepository;
import com.team.recommendations.service.mapper.StatsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private RuleCounterRepository ruleCounterRepository;

    @Mock
    private StatsMapper statsMapper;

    @Mock
    private DynamicRuleRepository dynamicRuleRepository;

    private StatsService statsService;

    @BeforeEach
    void setUp() {
        statsService = new StatsService(
                ruleCounterRepository,
                statsMapper,
                dynamicRuleRepository
        );
    }

    @Test
    @DisplayName("Получает всю статистику")
    void testGetAllStats() {
        // given
        UUID ruleId1 = UUID.randomUUID();
        UUID ruleId2 = UUID.randomUUID();

        RuleCounter counter1 = new RuleCounter();
        counter1.setRuleId(ruleId1);
        counter1.setCount(10L);

        RuleCounter counter2 = new RuleCounter();
        counter2.setRuleId(ruleId2);
        counter2.setCount(20L);

        List<RuleCounter> counters = Arrays.asList(counter1, counter2);
        StatsDto expectedDto = new StatsDto();

        when(ruleCounterRepository.findAll()).thenReturn(counters);
        when(dynamicRuleRepository.findAll()).thenReturn(Collections.emptyList());
        when(statsMapper.toDtoWithMissingRules(any(Stats.class), any())).thenReturn(expectedDto);

        // when
        StatsDto result = statsService.getAllStats();

        // then
        assertNotNull(result);
        assertEquals(expectedDto, result);

        verify(ruleCounterRepository, times(1)).findAll();
        verify(statsMapper, times(1)).toDtoWithMissingRules(any(Stats.class), any());
    }

    @Test
    @DisplayName("Обновляет счетчик")
    void testChangeCounter() {
        // given
        UUID ruleId = UUID.randomUUID();
        Long newCount = 15L;

        // when
        statsService.changeCounter(ruleId, newCount);

        // then
        verify(ruleCounterRepository, times(1)).changeCounter(ruleId, newCount);
    }

    @Test
    @DisplayName("Удаляет всю статистику")
    void testDeleteStats() {
        // when
        statsService.deleteStats();

        // then
        verify(ruleCounterRepository, times(1)).deleteAll();
    }

    @Test
    @DisplayName("Проверяет существование счетчика по ID")
    void testIsPresentById() {
        // given
        UUID ruleId = UUID.randomUUID();

        when(ruleCounterRepository.existsById(ruleId)).thenReturn(true);

        // when
        boolean result = statsService.isPresentById(ruleId);

        // then
        assertTrue(result);
        verify(ruleCounterRepository, times(1)).existsById(ruleId);
    }

    @Test
    @DisplayName("Получает счетчик по ID")
    void testGetCountById() {
        // given
        UUID ruleId = UUID.randomUUID();
        RuleCounter counter = new RuleCounter();
        counter.setCount(25L);

        when(ruleCounterRepository.findById(ruleId)).thenReturn(java.util.Optional.of(counter));

        // when
        long result = statsService.getCountById(ruleId);

        // then
        assertEquals(25L, result);
        verify(ruleCounterRepository, times(1)).findById(ruleId);
    }

    @Test
    @DisplayName("Сохраняет новый счетчик")
    void testSaveNewCounter() {
        // given
        RuleCounter counter = new RuleCounter();
        counter.setRuleId(UUID.randomUUID());
        counter.setCount(5L);

        // when
        statsService.saveNewCounter(counter);

        // then
        verify(ruleCounterRepository, times(1)).save(counter);
    }
}