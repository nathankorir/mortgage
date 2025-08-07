package com.mortgage.backend.dto;

import com.mortgage.backend.enums.Enum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private UUID id;
    private Long nationalId;
    private String purpose;
    private Double amount;
    private Enum.ApplicationStatus status;
    private List<DocumentResponse> documents;
    private LocalDateTime createdAt;
}
