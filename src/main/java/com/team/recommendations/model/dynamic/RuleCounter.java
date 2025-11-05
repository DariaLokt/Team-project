package com.team.recommendations.model.dynamic;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "stats")
public class RuleCounter {
    @Id
    private UUID rule_id;
    private Long count;

    public RuleCounter() {
    }

    public UUID getRule_id() {
        return rule_id;
    }

    public void setRule_id(UUID rule_id) {
        this.rule_id = rule_id;
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
        return count == that.count && Objects.equals(rule_id, that.rule_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule_id, count);
    }

    @Override
    public String toString() {
        return "RuleCounter{" +
                "rule_id=" + rule_id +
                ", count=" + count +
                '}';
    }
}
