package com.team.recommendations.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Repository that uses read-only database with users and their transactions
 * Query results are put in cache
 */
@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final Cache<String, Object> resultsCache;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.resultsCache = Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    public Cache<String, Object> getResultsCache() {
        return resultsCache;
    }

    public boolean getUse(UUID userId, String productType) {
        String cacheKey = "getUse:" + userId + ":" + productType;
        return (Boolean) resultsCache.get(cacheKey, key -> {
            Integer count = jdbcTemplate.queryForObject(
                    """
                            SELECT COUNT (p.TYPE) AS Count
                            FROM transactions t
                            INNER JOIN products p ON t.product_id = p.id
                            WHERE t.user_id = ? AND p.TYPE = ?""",
                    Integer.class,
                    userId, productType);
            return count != null && count != 0;
        });
    }

    public boolean getActiveUse(UUID userId, String productType) {
        String cacheKey = "getActiveUse:" + userId + ":" + productType;
        return (Boolean) resultsCache.get(cacheKey, key -> {
            Integer count = jdbcTemplate.queryForObject(
                    """
                            SELECT COUNT (p.TYPE) AS Count
                            FROM transactions t
                            INNER JOIN products p ON t.product_id = p.id
                            WHERE t.user_id = ? AND p.TYPE = ?""",
                    Integer.class,
                    userId, productType);
            return count != null && count >= 5;
        });
    }

    public int getTransactionSum(UUID userId, String productType, String transactionType){
        String cacheKey = "getTransactionSum:" + userId + ":" + productType + ":" + transactionType;
        return (Integer) resultsCache.get(cacheKey, key -> {
            Integer transactionSum = jdbcTemplate.queryForObject(
                    """
                            SELECT SUM(t.amount)
                            FROM transactions t
                            INNER JOIN products p ON t.product_id = p.id
                            WHERE t.user_id = ? AND p.TYPE = ? AND t.TYPE = ?""",
                    Integer.class,
                    userId, productType, transactionType);
            return transactionSum != null ? transactionSum : 0;
        });
    }

    public boolean checkUserName(String userName) {
        String cacheKey = "checkUserName:" + userName;
        return (Boolean) resultsCache.get(cacheKey, key -> {
            Integer count = jdbcTemplate.queryForObject(
                    """
                            SELECT COUNT(*) FROM users WHERE USERNAME = ?""",
                    Integer.class,
                    userName);
            return count != null && count == 1;
        });
    }

    public Optional<String> getFullNameByUserName(String userName) {
        String cacheKey = "getFullNameByUserName:" + userName;
        return (Optional<String>) resultsCache.get(cacheKey, key -> {
            try {
                String firstName = jdbcTemplate.queryForObject(
                        """
                                SELECT first_name FROM users WHERE USERNAME = ?""",
                        String.class,
                        userName);
                String lastName = jdbcTemplate.queryForObject(
                        """
                                SELECT last_name FROM users WHERE USERNAME = ?""",
                        String.class,
                        userName);
                return Optional.of(firstName + " " + lastName);
            } catch (EmptyResultDataAccessException e) {
                return Optional.empty();
            }
        });
    }

    public UUID getIdByUserName(String userName) {
        String cacheKey = "getIdByUserName:" + userName;
        return (UUID) resultsCache.get(cacheKey, key -> jdbcTemplate.queryForObject(
                """
                        SELECT id FROM USERS WHERE USERNAME = ?""",
                UUID.class,
                userName));
    }
}