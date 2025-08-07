package com.mortgage.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private String name;
    private String type;
    private Long size;
    private String presignedUrl;
}
