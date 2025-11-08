package com.team.recommendations.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.team.recommendations.repository.RecommendationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CacheService {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RecommendationsRepository recommendationsRepository;

    Logger logger = LoggerFactory.getLogger(CacheService.class);

    public void clearManualCache(Cache<String, Object> cache) {
        cache.invalidateAll();
        logger.info("Manual cache was cleared");
    }

    public void evictAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        clearManualCache(recommendationsRepository.getResultsCache());
        logger.info("All caches cleared successfully");
    }
}
