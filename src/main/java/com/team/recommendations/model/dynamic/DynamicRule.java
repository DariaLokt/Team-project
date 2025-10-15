package com.team.recommendations.model.dynamic;

import java.util.ArrayList;
import java.util.Objects;

public class DynamicRule {
    private final String query;
    private final ArrayList<String> arguments;
    private final Boolean negate;

    public DynamicRule(String query, ArrayList<String> arguments, Boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }

    public String getQuery() {
        return query;
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    public Boolean getNegate() {
        return negate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DynamicRule that = (DynamicRule) o;
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
