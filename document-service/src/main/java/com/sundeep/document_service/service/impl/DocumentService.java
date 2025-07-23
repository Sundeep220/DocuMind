package com.sundeep.document_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sundeep.document_service.client.UserServiceClient;
import com.sundeep.document_service.dto.DocumentResponseDto;
import com.sundeep.document_service.dto.DocumentUploadResponse;
import com.sundeep.document_service.dto.UserResponseDTO;
import com.sundeep.document_service.entity.DocumentMetadata;
import com.sundeep.document_service.entity.DocumentStatus;
import com.sundeep.document_service.exceptions.DocumentNotFoundException;
import com.sundeep.document_service.repo.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    @Autowired
    private final DocumentRepository docRepo;
    @Autowired
    private final FileStorageService fileStorage;
    @Autowired
    private final KafkaProducerService kafkaProducer;
    @Autowired
    private UserServiceClient userService;


    public DocumentUploadResponse upload(MultipartFile file, String jwtToken) throws JsonProcessingException {
        String path = fileStorage.storeFile(file);

        UserResponseDTO userProfile = userService.getUserProfile(jwtToken).getBody();
        if (userProfile == null) {
            throw new AccessDeniedException("Invalid User");
        }
        DocumentMetadata doc = DocumentMetadata.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .storagePath(path)
                .uploadedBy(userProfile.getEmail())
                .uploadedAt(LocalDateTime.now())
                .status(DocumentStatus.UPLOADED)
                .build();

        DocumentMetadata saved = docRepo.save(doc);
        kafkaProducer.sendDocumentEvent(saved);

        return new DocumentUploadResponse(saved.getId(), "Document uploaded and processing started");
    }

    public List<DocumentResponseDto> getMyDocuments(String jwtToken) {
        UserResponseDTO userProfile = userService.getUserProfile(jwtToken).getBody();
        if (userProfile == null) {
            throw new AccessDeniedException("Invalid User");
        }
        String userEmail = userProfile.getEmail();
        return docRepo.findByUploadedBy(userEmail).stream().map(this::toDto).toList();
    }

    public DocumentResponseDto getDocumentById(Long id, String jwtToken) {
        DocumentMetadata doc = docRepo.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
        UserResponseDTO userProfile = userService.getUserProfile(jwtToken).getBody();
        if (userProfile == null) {
            throw new AccessDeniedException("Invalid User");
        }
        String userEmail = userProfile.getEmail();
        if (!doc.getUploadedBy().equals(userEmail)) throw new AccessDeniedException("Unauthorized");
        return toDto(doc);
    }

    public void deleteDocument(Long id, String jwtToken) {
        DocumentMetadata doc = docRepo.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
        UserResponseDTO userProfile = userService.getUserProfile(jwtToken).getBody();
        if (userProfile == null) {
            throw new AccessDeniedException("Invalid User");
        }
        String userEmail = userProfile.getEmail();
        if (!doc.getUploadedBy().equals(userEmail)) throw new AccessDeniedException("Unauthorized");
        docRepo.deleteById(id);
    }

    private DocumentResponseDto toDto(DocumentMetadata d) {
        DocumentResponseDto dto = new DocumentResponseDto();
        dto.setId(d.getId());
        dto.setFileName(d.getFileName());
        dto.setFileType(d.getFileType());
        dto.setStatus(d.getStatus().name());
        dto.setUploadedBy(d.getUploadedBy());
        dto.setUploadedAt(d.getUploadedAt());
        return dto;
    }
}
