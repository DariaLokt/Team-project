package com.team.recommendations.service.mapper;

import com.team.recommendations.model.dynamic.DynamicProduct;
import com.team.recommendations.model.dynamic.DynamicProductDto;
import com.team.recommendations.model.dynamic.DynamicRule;
import com.team.recommendations.model.dynamic.DynamicRuleDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DynamicProductMapper {
    private final DynamicRuleMapper ruleMapper;

    public DynamicProductMapper(DynamicRuleMapper ruleMapper) {
        this.ruleMapper = ruleMapper;
    }

    public DynamicProductDto toDto(DynamicProduct entity) {
        if (entity == null) {
            return null;
        }

        DynamicProductDto dto = new DynamicProductDto();
        dto.setId(entity.getId());
        dto.setProductName(entity.getProductName());
        dto.setProductId(entity.getProductId());
        dto.setProductText(entity.getProductText());

        if (entity.getRule() != null) {
            Collection<DynamicRuleDto> ruleDto = ruleMapper.toDtoList(entity.getRule());
            dto.setRule(ruleDto);
        }

        return dto;
    }

    public DynamicProduct toEntity(DynamicProductDto dto) {
        if (dto == null) {
            return null;
        }

        DynamicProduct entity = new DynamicProduct();
        entity.setId(dto.getId());
        entity.setProductName(dto.getProductName());
        entity.setProductId(dto.getProductId());
        entity.setProductText(dto.getProductText());

        if (dto.getRule() != null) {
            Collection<DynamicRule> rules = ruleMapper.toEntityList(dto.getRule(), entity);
            entity.setRule(rules);
        }

        return entity;
    }

    public List<DynamicProductDto> toDtoList(Collection<DynamicProduct> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<DynamicProduct> toEntityList(Collection<DynamicProductDto> dto) {
        if (dto == null) {
            return Collections.emptyList();
        }
        return dto.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
