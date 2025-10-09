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

    public boolean getUse(UUID user_id, String productType) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT (p.TYPE) AS Count
                        FROM transactions t
                        INNER JOIN products p ON t.product_id = p.id
                        WHERE t.user_id = ? AND p.TYPE = ?""",
                Integer.class,
                user_id, productType);
        if (count != null) {
            return count != 0;
        } else {
            return false;
        }
    }

    public int getTransactionSum(UUID user_id, String productType, String transactionType){
        Integer transactionSum = jdbcTemplate.queryForObject(
                """
                        SELECT SUM(t.amount)
                        FROM transactions t
                        INNER JOIN products p ON t.product_id = p.id
                        WHERE t.user_id = ? AND p.TYPE = ? AND t.TYPE = ?""",
                Integer.class,
                user_id, productType, transactionType);
        if (transactionSum != null) {
            return transactionSum;
        } else {
            return 0;
        }
    }
}