package com.sundeep.document_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private String storagePath;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private DocumentStatus status; // UPLOADED, EMBEDDING_IN_PROGRESS, INDEXED, FAILED
}
