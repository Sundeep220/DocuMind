package com.sundeep.document_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sundeep.document_service.entity.DocumentMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private final KafkaTemplate<Long, String> kafkaTemplate;

    public void sendDocumentEvent(DocumentMetadata doc) throws JsonProcessingException {
        log.info("Sending document event for documentId: {}", doc.getId());
        Long key = doc.getId();
        String value = objectMapper.writeValueAsString(doc);
        log.info("Document event key with value {}", key);
        log.info("Document event value with value {}", value);
        log.info("Document event topic name {}", topicName);
        var producerRecord = buildProducerRecord(key, value, topicName);
        kafkaTemplate.send(producerRecord);
        log.info("Document event sent for documentId: {}", doc.getId());
    }

    private ProducerRecord<Long, String> buildProducerRecord(Long key, String value, String topic) {
        return new ProducerRecord<>(topic, key, value);
    }
}
