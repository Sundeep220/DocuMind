package com.sundeep.document_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data

public class DocumentResponseDto {
    private Long id;
    private String fileName;
    private String fileType;
    private String uploadedBy;
    private String userId;
    private String status;
    private LocalDateTime uploadedAt;
}
