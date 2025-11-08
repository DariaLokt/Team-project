package com.team.recommendations.service.mapper;

import com.team.recommendations.model.stats.RuleCounter;
import com.team.recommendations.model.stats.RuleCounterDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RuleCounterMapper {
    public RuleCounterDto toDto(RuleCounter entity) {
        if (entity == null) {
            return null;
        }
        RuleCounterDto dto = new RuleCounterDto();
        dto.setRuleId(entity.getRuleId());
        dto.setCount(entity.getCount());
        return dto;
    }

    public RuleCounter toEntity(RuleCounterDto dto) {
        if (dto == null) {
            return null;
        }
        RuleCounter entity = new RuleCounter();
        entity.setRuleId(dto.getRuleId());
        entity.setCount(dto.getCount());
        return entity;
    }

    public List<RuleCounterDto> toDtoList(Collection<RuleCounter> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<RuleCounter> toEntityList(Collection<RuleCounterDto> dtos) {
        if (dtos == null) {
            return Collections.emptyList();
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
