package com.team.recommendations.service.mapper;

import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.model.dynamic.DynamicRule;
import com.team.recommendations.model.dynamic.DynamicRuleDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DynamicRuleMapper {
    public DynamicRuleDto toDto(DynamicRule entity) {
        if (entity == null) {
            return null;
        }

        DynamicRuleDto dto = new DynamicRuleDto();
        dto.setQuery(entity.getQuery());
        dto.setArguments(entity.getArguments());
        dto.setNegate(entity.getNegate());
        return dto;
    }

    public DynamicRule toEntity(DynamicRuleDto dto) {
        return toEntity(dto, null);
    }

    public DynamicRule toEntity(DynamicRuleDto dto, DynamicProduct product) {
        if (dto == null) {
            return null;
        }

        DynamicRule entity = new DynamicRule();
        entity.setQuery(dto.getQuery());
        entity.setArguments(dto.getArguments());
        entity.setNegate(dto.getNegate());
        entity.setProduct(product);
        return entity;
    }

    public List<DynamicRuleDto> toDtoList(Collection<DynamicRule> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<DynamicRule> toEntityList(Collection<DynamicRuleDto> dto) {
        return toEntityList(dto, null);
    }

    public List<DynamicRule> toEntityList(Collection<DynamicRuleDto> dto, DynamicProduct product) {
        if (dto == null) {
            return Collections.emptyList();
        }
        return dto.stream()
                .map(d -> toEntity(d, product))
                .collect(Collectors.toList());
    }
}
