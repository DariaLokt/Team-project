package com.team.recommendations.repository;

import com.team.recommendations.model.dynamic.DynamicProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DynamicProductRepository extends JpaRepository<DynamicProduct, UUID> {
    Optional<DynamicProduct> findByProductId(UUID productId);
}
