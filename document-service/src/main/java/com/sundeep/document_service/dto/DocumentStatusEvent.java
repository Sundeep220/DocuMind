package com.sundeep.document_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class DocumentStatusEvent {
    private Long documentId;
    private String status;
}
