package com.team.recommendations.model.dynamic;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;
import java.util.Objects;

@Schema(description = "Модель данных динамического правила")
public class DynamicRuleDto {
    @Schema(
            type = "string",
            description = "Тип динамического правила"
    )
    private String query;
    @Schema(
            type = "array",
            description = "Аргументы динамического правила"
    )
    private Collection<String> arguments;
    @Schema(
            type = "boolean",
            description = "Используется ли отрицание в динамическом правиле"
    )
    private Boolean negate;

    public DynamicRuleDto() {
    }

    public DynamicRuleDto(String query, Collection<String> arguments, Boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Collection<String> getArguments() {
        return arguments;
    }

    public void setArguments(Collection<String> arguments) {
        this.arguments = arguments;
    }

    public Boolean getNegate() {
        return negate;
    }

    public void setNegate(Boolean negate) {
        this.negate = negate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DynamicRuleDto that = (DynamicRuleDto) o;
        return Objects.equals(query, that.query) && Objects.equals(arguments, that.arguments) && Objects.equals(negate, that.negate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, arguments, negate);
    }

    @Override
    public String toString() {
        return "DynamicRule{" +
                "query='" + query + '\'' +
                ", arguments=" + arguments +
                ", negate=" + negate +
                '}';
    }
}
