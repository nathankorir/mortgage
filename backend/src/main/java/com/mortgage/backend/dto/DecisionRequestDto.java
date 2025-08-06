package com.mortgage.backend.dto;

import com.mortgage.backend.enums.Enum.DecisionType;
import com.mortgage.backend.enums.Enum.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecisionRequestDto {
//    @NotNull(message = "Application ID is required")
//    private UUID applicationId;
//
    @NotNull(message = "Decision type is required")
    private DecisionType decisionType;

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    @NotNull(message = "Approver ID is required")
    private UUID approverId;

    @NotBlank(message = "Comments are required")
    private String comments;
}
