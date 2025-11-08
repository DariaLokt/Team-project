package com.team.recommendations.repository;

import com.team.recommendations.model.stats.RuleCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RuleCounterRepository extends JpaRepository<RuleCounter, UUID> {
    @Modifying
    @Query(value = "UPDATE stats SET count = :newCount WHERE rule_id = :id", nativeQuery = true)
    void changeCounter(@Param("id")UUID id, @Param("newCount")Long newCount);
}
