package com.proyecto.mensajeria.messaging;

import com.proyecto.mensajeria.config.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class KafkaDltAuditListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaDltAuditListener.class);

    @KafkaListener(
            topics = {KafkaConfig.TOPIC_ASISTENCIA_DLT, KafkaConfig.TOPIC_NOTAS_DLT},
            groupId = KafkaConfig.GROUP_ID + "-dlt",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void onDltMessage(ConsumerRecord<String, String> record) {
        String originalTopic = extractHeader(record, KafkaHeaders.DLT_ORIGINAL_TOPIC);
        String motivo = extractHeader(record, KafkaHeaders.DLT_EXCEPTION_MESSAGE);
        log.error("[KAFKA DLT AUDITORIA] Topico origen: {}, Motivo: {}, Payload: {}",
                originalTopic, motivo, record.value());
    }

    private String extractHeader(ConsumerRecord<String, String> record, String headerName) {
        Header header = record.headers().lastHeader(headerName);
        if (header == null || header.value() == null) {
            return "desconocido";
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }
}

