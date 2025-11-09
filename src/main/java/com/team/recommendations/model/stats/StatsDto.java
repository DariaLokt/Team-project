package com.team.recommendations.model.stats;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;
import java.util.Objects;

@Schema(description = "Модель данных статистики использования правил")
public class StatsDto {
    @Schema(
            type = "array",
            description = "Статистика использования всех правил"
    )
    private Collection<RuleCounterDto> stats;

    public StatsDto() {
    }

    public StatsDto(Collection<RuleCounterDto> stats) {
        this.stats = stats;
    }

    public Collection<RuleCounterDto> getStats() {
        return stats;
    }

    public void setStats(Collection<RuleCounterDto> stats) {
        this.stats = stats;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StatsDto stats1 = (StatsDto) o;
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
