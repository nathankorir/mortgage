package com.mortgage.backend.repository;

import com.mortgage.backend.model.Application;
import com.mortgage.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID>, QuerydslPredicateExecutor<Application> {
    List<Application> findByApplicant(User applicant);
}