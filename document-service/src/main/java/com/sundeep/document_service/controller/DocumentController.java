package com.sundeep.document_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sundeep.document_service.dto.DocumentResponseDto;
import com.sundeep.document_service.dto.DocumentUploadResponse;
import com.sundeep.document_service.service.impl.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String jwtToken
    ) throws JsonProcessingException {
        DocumentUploadResponse response = documentService.upload(file, jwtToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponseDto>> listMyDocs(@RequestHeader("Authorization") String jwtToken) {
        return ResponseEntity.ok(documentService.getMyDocuments(jwtToken));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDto> getById(@PathVariable Long id, @RequestHeader("Authorization") String jwtToken) {
        return ResponseEntity.ok(documentService.getDocumentById(id, jwtToken));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestHeader("Authorization") String jwtToken) {
        documentService.deleteDocument(id, jwtToken);
        return ResponseEntity.ok().build();
    }
}
