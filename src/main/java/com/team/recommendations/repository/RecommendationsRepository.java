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

    public boolean getActiveUse(UUID user_id, String productType) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT (p.TYPE) AS Count
                        FROM transactions t
                        INNER JOIN products p ON t.product_id = p.id
                        WHERE t.user_id = ? AND p.TYPE = ?""",
                Integer.class,
                user_id, productType);
        if (count != null) {
            return count >= 5;
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

    public boolean checkUserName(String userName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM users WHERE USERNAME = ?""",
                Integer.class,
                userName);
        if (count != null) {
            return count == 1;
        } else {
            return false;
        }
    }

    public String getFullNameByUserName(String userName) {
        String firstName = jdbcTemplate.queryForObject(
                """
                        SELECT first_name FROM users WHERE USERNAME = ?""",
                String.class,
                userName);
        String lastName = jdbcTemplate.queryForObject(
                """
                        SELECT first_name FROM users WHERE USERNAME = ?""",
                String.class,
                userName);
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else {
            return null;
        }
    }

    public UUID getIdByUserName(String userName) {
        UUID id = jdbcTemplate.queryForObject(
                """
                        SELECT id FROM USERS WHERE USERNAME = ?""",
                UUID.class,
                userName);
        return id;
    }
}