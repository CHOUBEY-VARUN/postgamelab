package com.postgamelab.breakdown;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BreakdownRepository extends JpaRepository<Breakdown, UUID> {

    Optional<Breakdown> findBySlug(String slug);
}
