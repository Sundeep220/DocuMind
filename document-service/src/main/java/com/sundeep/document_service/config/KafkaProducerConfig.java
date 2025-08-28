package com.sundeep.document_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;


    @Bean
    public ProducerFactory<Long, String> producerFactory() throws ClassNotFoundException {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Class.forName(keySerializer));
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Class.forName(valueSerializer));
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<Long, String> kafkaTemplate() throws ClassNotFoundException {
        return new KafkaTemplate<>(producerFactory());
    }


    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(topicName)
                .build();
    }
}
