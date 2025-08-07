package com.mortgage.backend.service;

import com.mortgage.backend.dto.ApplicationRequest;
import com.mortgage.backend.dto.ApplicationResponse;
import com.mortgage.backend.dto.DecisionRequestDto;
import com.mortgage.backend.dto.KafkaMessageDto;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final DecisionRepository decisionRepository;
    private final UserRepository userRepository;
    private final ApplicationMapper applicationMapper;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${spring.kafka.topic}")
    private String topic;

    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository, DecisionRepository decisionRepository, ApplicationMapper applicationMapper, KafkaTemplate kafkaTemplate) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.decisionRepository = decisionRepository;
        this.applicationMapper = applicationMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public ApplicationResponse create(ApplicationRequest dto) {
        logger.info("Creating application service");
        User user = userRepository.findByNationalId(dto.getNationalId()).orElseThrow(() -> new NoSuchElementException("User not found"));
        logger.info("Creating application got user");
        Application application = applicationMapper.toEntity(dto);
        application.setApplicant(user);
        Application created = applicationRepository.save(application);
        produceKafkaMessage(KafkaMessageDto.builder().application(created).build());
        return applicationMapper.toDto(created);
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
        decision.setApplication(application);
        decision.setDecisionType(dto.getDecisionType());
        decision.setApprover(approver);
        decision.setComments(dto.getComments());

        application.setStatus(dto.getStatus());

        decisionRepository.save(decision); // save decision first (if cascade is not used)
        Application updated = applicationRepository.save(application);
        produceKafkaMessage(KafkaMessageDto.builder().application(updated).build());

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
            predicate = predicate.and(qApplication.applicant.nationalId.eq(nationalId));
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

    private void produceKafkaMessage(KafkaMessageDto kafkaMessageDto) {
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put(String.valueOf(kafkaMessageDto.getApplication().getId()), kafkaMessageDto.toString());
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, messageMap.toString());
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent message to kafka");
            } else {
                logger.error("Failed to send message " + ex.getMessage());
            }
        });
    }
}
