package com.mortgage.backend.dto;

import com.mortgage.backend.enums.Enum.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KafkaMessageDto {
    private UUID applicationId;
    private Double amount;
    private String purpose;
    private ApplicationStatus status;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String traceId = UUID.randomUUID().toString();
    private String version = "1.0";
}
