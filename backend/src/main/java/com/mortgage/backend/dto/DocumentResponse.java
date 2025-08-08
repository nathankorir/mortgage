package com.mortgage.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private String fileName;
    private String type;
    private Long size;
    private String presignedUrl;
}
