package com.sundeep.document_service.repo;

import com.sundeep.document_service.entity.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentMetadata, Long> {
    List<DocumentMetadata> findByUploadedBy(String email);
}

