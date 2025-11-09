package com.team.recommendations.service.rules;

import com.team.recommendations.model.dynamic.DynamicRule;
import com.team.recommendations.model.stats.RuleCounter;
import com.team.recommendations.model.stats.Stats;
import com.team.recommendations.model.stats.StatsDto;
import com.team.recommendations.repository.DynamicRuleRepository;
import com.team.recommendations.repository.RuleCounterRepository;
import com.team.recommendations.service.mapper.StatsMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service for interacting with stats
 *
 * @author dlok
 * @version 1.0
 */
@Service
public class StatsService {
    public final RuleCounterRepository ruleCounterRepository;
    public final StatsMapper statsMapper;
    public final DynamicRuleRepository dynamicRuleRepository;

    public StatsService(RuleCounterRepository ruleCounterRepository, StatsMapper statsMapper, DynamicRuleRepository dynamicRuleRepository) {
        this.ruleCounterRepository = ruleCounterRepository;
        this.statsMapper = statsMapper;
        this.dynamicRuleRepository = dynamicRuleRepository;
    }

    /**
     * Method returns all stats from the repository and also maps them to dto
     * @return all stats in dto form
     */
    public StatsDto getAllStats() {
        List<RuleCounter> existingStats = ruleCounterRepository.findAll();
        List<DynamicRule> allRules = dynamicRuleRepository.findAll();

        return statsMapper.toDtoWithMissingRules(
                new Stats(existingStats),
                allRules
        );
    }

    public void deleteStats() {
        ruleCounterRepository.deleteAll();
    }

    /**
     * Method updates counter
     * @param id old counter id
     * @param newCount new counter to replace the old one
     */
    public void changeCounter(UUID id, Long newCount) {
        ruleCounterRepository.changeCounter(id, newCount);
    }

    public boolean isPresentById(UUID id) {
        return ruleCounterRepository.existsById(id);
    }

    public long getCountById(UUID id) {
        return ruleCounterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rule counter not found for ID: " + id))
                .getCount();
    }

    public void saveNewCounter(RuleCounter ruleCounter) {
        ruleCounterRepository.save(ruleCounter);
    }
}
