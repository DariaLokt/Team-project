package com.team.recommendations.model.rules;

public class CompareRule implements Rule{
    private final int firstValue;
    private final String comparator;
    private final int secondValue;

    public CompareRule(int firstValue, String comparator, int secondValue) {
        this.firstValue = firstValue;
        this.comparator = comparator;
        this.secondValue = secondValue;
    }

    @Override
    public Boolean isFollowed() {
        return switch (comparator) {
            case ">" -> firstValue > secondValue;
            case ">=" -> firstValue >= secondValue;
            case "<" -> firstValue < secondValue;
            case "<=" -> firstValue <= secondValue;
            case "=" -> firstValue == secondValue;
            default -> throw new RuntimeException("No such comparator");
        };
    }
}
