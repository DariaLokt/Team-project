package com.team.recommendations.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getRandomTransactionAmount(UUID user){
        Integer result = jdbcTemplate.queryForObject(
                "SELECT amount FROM transactions t WHERE t.user_id = ? LIMIT 1",
                Integer.class,
                user);
        return result != null ? result : 0;
    }
    public boolean getDebitUse() {
        return true;
    }
    public boolean getInvestUse(){
        return false;
    };
    public int getSavingDeposit(){
        return 1000000;
    };
    public int getDebitDeposit(){
        return 10000000;
    };
    public int getDebitWithdraw(){
        return 1000000;
    };
    public boolean getCreditUse(){
        return false;
    };
}