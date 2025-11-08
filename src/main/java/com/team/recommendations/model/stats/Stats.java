package com.team.recommendations.model.stats;

import java.util.Collection;
import java.util.Objects;

public class Stats {
    private Collection<RuleCounter> stats;

    public Stats(Collection<RuleCounter> stats) {
        this.stats = stats;
    }

    public Collection<RuleCounter> getStats() {
        return stats;
    }

    public void setStats(Collection<RuleCounter> stats) {
        this.stats = stats;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Stats stats1 = (Stats) o;
        return Objects.equals(stats, stats1.stats);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stats);
    }

    @Override
    public String toString() {
        return "Stats{" +
                "stats=" + stats +
                '}';
    }
}
