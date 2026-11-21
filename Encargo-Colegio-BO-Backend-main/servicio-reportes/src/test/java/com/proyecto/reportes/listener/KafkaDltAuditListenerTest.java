package com.proyecto.reportes.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.KafkaHeaders;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class KafkaDltAuditListenerTest {

    private final KafkaDltAuditListener listener = new KafkaDltAuditListener();

    @Test
    void onDltMessage_ConHeadersCompletosNoLanzaExcepcion() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("notas.eventos.DLT", 0, 0L, "1-1", "{\"bad\":}");
        record.headers().add(KafkaHeaders.DLT_ORIGINAL_TOPIC, "notas.eventos".getBytes(StandardCharsets.UTF_8));
        record.headers().add(KafkaHeaders.DLT_EXCEPTION_MESSAGE, "JSON parse error".getBytes(StandardCharsets.UTF_8));

        assertDoesNotThrow(() -> listener.onDltMessage(record));
    }

    @Test
    void onDltMessage_SinHeadersNoLanzaExcepcion() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("asistencia.eventos.DLT", 0, 1L, "1", "raw-invalid");
        assertDoesNotThrow(() -> listener.onDltMessage(record));
    }
}

