package com.proyecto.ColegioBackend.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    public static final String TOPIC_ASISTENCIA = "asistencia.eventos";
    public static final String TOPIC_ASISTENCIA_DLT = "asistencia.eventos.DLT";
    public static final String TOPIC_NOTAS = "notas.eventos";
    public static final String TOPIC_NOTAS_DLT = "notas.eventos.DLT";

    @Value("${SPRING_KAFKA_BOOTSTRAP_SERVERS:localhost:9092,localhost:9093}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic asistenciaEventosTopic() {
        return TopicBuilder.name(TOPIC_ASISTENCIA)
                .partitions(3)
                .replicas(2)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.RETENTION_MS_CONFIG, "604800000")
                .config(TopicConfig.RETENTION_BYTES_CONFIG, "104857600")
                .build();
    }

    @Bean
    public NewTopic notasEventosTopic() {
        return TopicBuilder.name(TOPIC_NOTAS)
                .partitions(3)
                .replicas(2)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
                .config(TopicConfig.RETENTION_MS_CONFIG, "2592000000")
                .config(TopicConfig.RETENTION_BYTES_CONFIG, "104857600")
                .build();
    }

    @Bean
    public NewTopic asistenciaDltTopic() {
        return TopicBuilder.name(TOPIC_ASISTENCIA_DLT)
                .partitions(1)
                .replicas(2)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.RETENTION_MS_CONFIG, "604800000")
                .build();
    }

    @Bean
    public NewTopic notasDltTopic() {
        return TopicBuilder.name(TOPIC_NOTAS_DLT)
                .partitions(1)
                .replicas(2)
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .config(TopicConfig.RETENTION_MS_CONFIG, "604800000")
                .build();
    }
}

