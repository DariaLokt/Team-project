package com.team.recommendations.model.dynamic;

public enum Comparator {
    GREATER_THAN(">"),
    LESS_THAN("<"),
    EQUALS("="),
    GREATER_OR_EQUAL(">="),
    LESS_OR_EQUAL("<=");

    private final String symbol;

    Comparator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Comparator fromString(String symbol) {
        for (Comparator comp : Comparator.values()) {
            if (comp.symbol.equals(symbol)) {
                return comp;
            }
        }
        throw new IllegalArgumentException("Unknown comparator: " + symbol);
    }
}
