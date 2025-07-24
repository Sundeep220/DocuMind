package com.sundeep.document_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component
public class DocumentEventConsumer {

//    @KafkaListener(topics = "doc-upload", groupId = "doc-test-consumer")
    public void consume(String message) {
        System.out.println("📥 Consumed event: " + message);
    }
}
