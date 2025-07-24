package com.sundeep.document_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sundeep.document_service.dto.DocumentStatusEvent;
import com.sundeep.document_service.entity.DocumentMetadata;
import com.sundeep.document_service.entity.DocumentStatus;
import com.sundeep.document_service.repo.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaStatusConsumer {

    @Autowired
    private DocumentRepository documentMetadataRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "doc-status-events", groupId = "doc-status-updater")
    public void consumeStatusEvent(ConsumerRecord<Long, String> message) throws JsonProcessingException {
        try {
            log.info("Received Consumer Record: {}", message.value());
            DocumentStatusEvent docStatusEvent = objectMapper.readValue(message.value(), DocumentStatusEvent.class);
            log.info("Received status event: {}", docStatusEvent);


            if (docStatusEvent.getDocumentId() == null || docStatusEvent.getStatus() == null) {
                throw new RuntimeException("Invalid status event");
            }

            Long docId = docStatusEvent.getDocumentId();
            DocumentStatus newStatus = DocumentStatus.valueOf(docStatusEvent.getStatus());

            DocumentMetadata doc = documentMetadataRepository.findById(docId)
                    .orElseThrow(() -> new RuntimeException("Document not found"));

            doc.setStatus(newStatus);
            documentMetadataRepository.save(doc);
        }catch (Exception e) {
                throw new RuntimeException(e);
            }
    }
}
