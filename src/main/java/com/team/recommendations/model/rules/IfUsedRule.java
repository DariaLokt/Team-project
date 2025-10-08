package com.team.recommendations.model.rules;

public class IfUsedRule implements Rule{
    private final boolean real;
    private final boolean wish;

    public IfUsedRule(boolean real, boolean wish) {
        this.real = real;
        this.wish = wish;
    }

    @Override
    public Boolean isFollowed() {
        return real == wish;
    }
}
