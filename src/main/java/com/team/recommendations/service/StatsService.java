package com.team.recommendations.service;

import com.team.recommendations.model.stats.RuleCounter;
import com.team.recommendations.model.stats.Stats;
import com.team.recommendations.repository.StatsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StatsService {
    public final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public Stats getAllStats() {
        return new Stats(statsRepository.findAll());
    }

    public void deleteStats() {
        statsRepository.deleteAll();
    }

    public void changeCounter(UUID id, Long newCount) {
        statsRepository.changeCounter(id, newCount);
    }

    public boolean isPresentById(UUID id) {
        return statsRepository.existsById(id);
    }

    public long getCountById(UUID id) {
        return statsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rule counter not found for ID: " + id))
                .getCount();
    }

    public void saveNewCounter(RuleCounter ruleCounter) {
        statsRepository.save(ruleCounter);
    }
}
