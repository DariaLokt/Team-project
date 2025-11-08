package com.team.recommendations.model.stats;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "stats")
public class RuleCounter {
    @Id
    private UUID ruleId;
    private Long count;

    public RuleCounter() {
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
        RuleCounter that = (RuleCounter) o;
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
