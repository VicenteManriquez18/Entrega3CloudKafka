package com.proyecto.ColegioBackend.messaging;

import com.proyecto.ColegioBackend.config.KafkaConfig;
import com.proyecto.ColegioBackend.dto.NotaEventoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotaKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(NotaKafkaProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NotaKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarNota(NotaEventoDto dto) {
        String key = dto.getAlumnoId() + "-" + dto.getCursoId();
        kafkaTemplate.send(KafkaConfig.TOPIC_NOTAS, key, dto)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        logger.info("[KAFKA PRODUCER] Enviado a topic={} partition={} offset={} key={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                key);
                    } else {
                        logger.error("[KAFKA PRODUCER] Error enviando nota para key={}: {}",
                                key, ex.getMessage());
                    }
                });
    }
}

