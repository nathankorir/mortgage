package com.mortgage.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {
    @NotNull
    private String fileName;

    @NotNull
    private String type;

    @NotNull
    private Long size;

    @NotNull
    private String presignedUrl;
}
