package com.mortgage.backend.controller;

import com.mortgage.backend.dto.ApplicationRequest;
import com.mortgage.backend.dto.ApplicationResponse;
import com.mortgage.backend.dto.DecisionRequestDto;
import com.mortgage.backend.enums.Enum;
import com.mortgage.backend.service.ApplicationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {
    private final ApplicationService applicationService;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);


    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@RequestBody @Valid ApplicationRequest dto) {
        logger.info("Creating Application");
        return ResponseEntity.ok(applicationService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> get(@PathVariable UUID id) {
        Optional<ApplicationResponse> result = applicationService.get(id);
        return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<ApplicationResponse> getApplications(
            @RequestParam(required = false) Long nationalId,
            @RequestParam(required = false) Enum.ApplicationStatus status,
            @RequestParam(required = false) String purpose,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdTo,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return applicationService.search(nationalId, status, purpose, createdFrom, createdTo, pageable);
    }

    @PatchMapping("/{id}/decision")
    public ResponseEntity<ApplicationResponse> decide(@PathVariable UUID id, @RequestBody @Valid DecisionRequestDto dto) {
        return ResponseEntity.ok(applicationService.decide(id, dto));
    }
}
