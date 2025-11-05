package com.team.recommendations.service;

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
    private DynamicRecommendationService dynamicRecommendationService;

    Logger logger = LoggerFactory.getLogger(CacheService.class);

    public void evictAllCaches() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
        dynamicRecommendationService.clearManualCache();
        logger.info("All caches cleared successfully");
    }
}
