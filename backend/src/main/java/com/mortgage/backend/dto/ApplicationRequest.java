package com.mortgage.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequest {
    @NotNull
    private Long nationalId;
    @NotNull
    private String purpose;
    @NotNull
    private Double amount;
}
