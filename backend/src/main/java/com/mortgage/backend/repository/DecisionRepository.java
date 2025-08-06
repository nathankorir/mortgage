package com.mortgage.backend.repository;

import com.mortgage.backend.model.Application;
import com.mortgage.backend.model.Decision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, UUID> {
    Optional<Decision> findByApplication(Application application);
}