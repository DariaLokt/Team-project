package com.team.recommendations.model.stats;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Schema(description = "Модель данных подсчёта использования правила")
public class RuleCounterDto {
    @Schema(
            type = "uuid",
            description = "ID правила"
    )
    private UUID ruleId;
    @Schema(
            type = "integer",
            format = "int64",
            description = "Счётчик использования правила"
    )
    private Long count;

    public RuleCounterDto() {
    }

    public UUID getRuleId() {
        return ruleId;
    }

    public void setRuleId(UUID ruleId) {
        this.ruleId = ruleId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RuleCounterDto that = (RuleCounterDto) o;
        return count == that.count && Objects.equals(ruleId, that.ruleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, count);
    }

    @Override
    public String toString() {
        return "RuleCounter{" +
                "rule_id=" + ruleId +
                ", count=" + count +
                '}';
    }
}
