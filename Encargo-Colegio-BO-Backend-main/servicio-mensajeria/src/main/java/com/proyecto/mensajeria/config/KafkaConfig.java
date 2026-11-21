package com.proyecto.mensajeria.config;

import com.proyecto.mensajeria.dto.AsistenciaEventoDto;
import com.proyecto.mensajeria.dto.NotaEventoDto;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    public static final String TOPIC_ASISTENCIA = "asistencia.eventos";
    public static final String TOPIC_ASISTENCIA_DLT = "asistencia.eventos.DLT";
    public static final String TOPIC_NOTAS = "notas.eventos";
    public static final String TOPIC_NOTAS_DLT = "notas.eventos.DLT";
    public static final String GROUP_ID = "alertas-academicas-group";

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

        JsonSerializer<Object> jsonSerializer = new JsonSerializer<>();
        jsonSerializer.setAddTypeInfo(false);

        Map<Class<?>, Serializer<?>> delegates = new LinkedHashMap<>();
        delegates.put(byte[].class, new ByteArraySerializer());
        delegates.put(String.class, new StringSerializer());
        delegates.put(Object.class, jsonSerializer);

        return new DefaultKafkaProducerFactory<>(
                configProps,
                new StringSerializer(),
                new DelegatingByTypeSerializer(delegates, true)
        );
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public CommonErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", 0)
        );
        return new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 2L));
    }

    private <T> ConsumerFactory<String, T> buildConsumerFactory(Class<T> targetClass) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(targetClass, false);
        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new ErrorHandlingDeserializer<>(new StringDeserializer()),
                new ErrorHandlingDeserializer<>(jsonDeserializer)
        );
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> buildListenerFactory(
            Class<T> targetClass,
            CommonErrorHandler kafkaErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(buildConsumerFactory(targetClass));
        factory.setCommonErrorHandler(kafkaErrorHandler);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AsistenciaEventoDto> asistenciaKafkaListenerContainerFactory(
            CommonErrorHandler kafkaErrorHandler) {
        return buildListenerFactory(AsistenciaEventoDto.class, kafkaErrorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotaEventoDto> notaKafkaListenerContainerFactory(
            CommonErrorHandler kafkaErrorHandler) {
        return buildListenerFactory(NotaEventoDto.class, kafkaErrorHandler);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> dltKafkaListenerContainerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID + "-dlt");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new StringDeserializer()
        );
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cf);
        return factory;
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
