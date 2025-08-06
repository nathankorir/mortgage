package com.mortgage.backend.service;

import com.mortgage.backend.dto.ApplicationRequest;
import com.mortgage.backend.dto.ApplicationResponse;
import com.mortgage.backend.dto.DecisionRequestDto;
import com.mortgage.backend.enums.Enum.ApplicationStatus;
import com.mortgage.backend.mapper.ApplicationMapper;
import com.mortgage.backend.model.Application;
import com.mortgage.backend.model.Decision;
import com.mortgage.backend.model.QApplication;
import com.mortgage.backend.model.User;
import com.mortgage.backend.repository.ApplicationRepository;
import com.mortgage.backend.repository.DecisionRepository;
import com.mortgage.backend.repository.UserRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final DecisionRepository decisionRepository;
    private final UserRepository userRepository;
    private final ApplicationMapper applicationMapper;

    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository, DecisionRepository decisionRepository, ApplicationMapper applicationMapper) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.decisionRepository = decisionRepository;
        this.applicationMapper = applicationMapper;
    }

    public ApplicationResponse create(ApplicationRequest dto) {
        Application application = applicationMapper.toEntity(dto);
        return applicationMapper.toDto(applicationRepository.save(application));
    }

    public Optional<ApplicationResponse> get(UUID id) {
        return applicationRepository.findById(id).map(applicationMapper::toDto);
    }

    public Page<ApplicationResponse> search(
            Long nationalId,
            ApplicationStatus status,
            String purpose,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            Pageable pageable
    ) {
        BooleanExpression predicate = buildPredicate(nationalId, status, purpose, createdFrom, createdTo);
        return applicationRepository.findAll(predicate, pageable).map(applicationMapper::toDto);
    }

    public ApplicationResponse decide(UUID id, DecisionRequestDto dto) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Application not found"));

        User approver = userRepository.findById(dto.getApproverId())
                .orElseThrow(() -> new NoSuchElementException("Approver not found"));

        Decision decision = new Decision();
        decision.setApprover(approver);
        decision.setComments(dto.getComments());

        application.setStatus(dto.getStatus());

        decisionRepository.save(decision); // save decision first (if cascade is not used)
        applicationRepository.save(application);

        return applicationMapper.toDto(application);
    }

    private BooleanExpression buildPredicate(
            Long nationalId,
            ApplicationStatus status,
            String purpose,
            LocalDateTime createdFrom,
            LocalDateTime createdTo
    ) {
        QApplication qApplication = QApplication.application;
        BooleanExpression predicate = qApplication.isNotNull();

        if (nationalId != null) {
            predicate = predicate.and(qApplication.user.nationalId.eq(nationalId));
        }

        if (status != null) {
            predicate = predicate.and(qApplication.status.eq(status));
        }

        if (StringUtils.hasText(purpose)) {
            predicate = predicate.and(qApplication.purpose.equalsIgnoreCase(purpose));
        }

        if (createdFrom != null) {
            predicate = predicate.and(qApplication.createdAt.goe(createdFrom));
        }

        if (createdTo != null) {
            predicate = predicate.and(qApplication.createdAt.loe(createdTo));
        }

        return predicate;
    }
}
