package com.team.recommendations.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

//    TODO: Сделать тип операции через параметр
    public boolean getDebitUse(UUID user_id) {
        try {
            Integer countDebit = jdbcTemplate.queryForObject(
                    "SELECT COUNT (p.TYPE) AS Count\n" +
                            "FROM transactions t\n" +
                            "INNER JOIN products p ON t.product_id = p.id\n" +
                            "WHERE t.user_id = ? AND p.TYPE = 'DEBIT'",
                    Integer.class,
                    user_id);
            return countDebit != 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean getInvestUse(UUID user_id){
        try {
            Integer countInvest = jdbcTemplate.queryForObject(
                    "SELECT COUNT (p.TYPE) AS Count\n" +
                            "FROM transactions t\n" +
                            "INNER JOIN products p ON t.product_id = p.id\n" +
                            "WHERE t.user_id = ? AND p.TYPE = 'INVEST'",
                    Integer.class,
                    user_id);
            return countInvest != 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean getCreditUse(UUID user_id){
        try {
            Integer countCredit = jdbcTemplate.queryForObject(
                    "SELECT COUNT (p.TYPE) AS Count\n" +
                            "FROM transactions t\n" +
                            "INNER JOIN products p ON t.product_id = p.id\n" +
                            "WHERE t.user_id = ? AND p.TYPE = 'INVEST'",
                    Integer.class,
                    user_id);
            return countCredit != 0;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public int getSavingDeposit(UUID user_id){
        try {
            Integer savingDeposit = jdbcTemplate.queryForObject(
                    "SELECT SUM(t.amount)\n" +
                            "FROM transactions t\n" +
                            "INNER JOIN products p ON t.product_id = p.id\n" +
                            "WHERE t.user_id = ? AND p.TYPE = 'SAVING' AND t.TYPE = 'DEPOSIT'",
                    Integer.class,
                    user_id);
            return savingDeposit;
        } catch (DataAccessException e) {
            return 0;
        }
    }

    public int getDebitDeposit(UUID user_id){
        try {
            Integer debitDeposit = jdbcTemplate.queryForObject(
                    "SELECT SUM(t.amount)\n" +
                            "FROM transactions t\n" +
                            "INNER JOIN products p ON t.product_id = p.id\n" +
                            "WHERE t.user_id = ? AND p.TYPE = 'DEBIT' AND t.TYPE = 'DEPOSIT'",
                    Integer.class,
                    user_id);
            return debitDeposit;
        } catch (DataAccessException e) {
            return 0;
        }
    }

    public int getDebitWithdraw(UUID user_id){
        try {
            Integer debitWithdraw = jdbcTemplate.queryForObject(
                    "SELECT SUM(t.amount)\n" +
                            "FROM transactions t\n" +
                            "INNER JOIN products p ON t.product_id = p.id\n" +
                            "WHERE t.user_id = ? AND p.TYPE = 'DEBIT' AND t.TYPE = 'WITHDRAW'",
                    Integer.class,
                    user_id);
            return debitWithdraw;
        } catch (DataAccessException e) {
            return 0;
        }
    }
}