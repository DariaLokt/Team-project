package com.team.recommendations.service.mapper;

import com.team.recommendations.model.dynamic.DynamicRule;
import com.team.recommendations.model.stats.RuleCounter;
import com.team.recommendations.model.stats.RuleCounterDto;
import com.team.recommendations.model.stats.Stats;
import com.team.recommendations.model.stats.StatsDto;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class StatsMapper {
    private final RuleCounterMapper ruleCounterMapper;

    public StatsMapper(RuleCounterMapper ruleCounterMapper) {
        this.ruleCounterMapper = ruleCounterMapper;
    }

    public StatsDto toDto(Stats entity) {
        if (entity == null) {
            return null;
        }
        Collection<RuleCounterDto> statsDto = ruleCounterMapper.toDtoList(entity.getStats());
        return new StatsDto(statsDto);
    }

    public StatsDto toDtoWithMissingRules(Stats stats, Collection<DynamicRule> allRules) {
        if (allRules == null) {
            return toDto(stats);
        }

        Map<UUID, Long> zeroedStats = allRules.stream()
                .collect(Collectors.toMap(
                        DynamicRule::getId,
                        rule -> 0L
                ));

        if (stats != null && stats.getStats() != null) {
            Map<UUID, Long> existingStats = stats.getStats().stream()
                    .collect(Collectors.toMap(
                            RuleCounter::getRuleId,
                            RuleCounter::getCount
                    ));
            zeroedStats.putAll(existingStats);
        }

        Collection<RuleCounterDto> resultStats = zeroedStats.entrySet().stream()
                .map(e -> {
                    RuleCounterDto ruleCounterDto = new RuleCounterDto();
                    ruleCounterDto.setRuleId(e.getKey());
                    ruleCounterDto.setCount(e.getValue());
                    return ruleCounterDto;
                })
                .collect(Collectors.toList());

        return new StatsDto(resultStats);
    }

    private RuleCounter createRuleCounter(UUID ruleId, Map<UUID, Long> existingStats) {
        RuleCounter counter = new RuleCounter();
        counter.setRuleId(ruleId);
        counter.setCount(existingStats.getOrDefault(ruleId, 0L));
        return counter;
    }

    public Stats toEntity(StatsDto dto) {
        if (dto == null) {
            return null;
        }
        Collection<RuleCounter> statsEntities = ruleCounterMapper.toEntityList(dto.getStats());
        return new Stats(statsEntities);
    }
}
