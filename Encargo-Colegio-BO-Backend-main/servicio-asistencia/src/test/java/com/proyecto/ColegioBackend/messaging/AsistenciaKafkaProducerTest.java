package com.proyecto.ColegioBackend.messaging;

import com.proyecto.ColegioBackend.config.KafkaConfig;
import com.proyecto.ColegioBackend.dto.AsistenciaEventoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsistenciaKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AsistenciaKafkaProducer producer;

    @Test
    void enviarAsistencia_UsaAlumnoIdComoKey() {
        AsistenciaEventoDto dto = AsistenciaEventoDto.builder()
                .asistenciaId(10L)
                .alumnoId(5L)
                .cursoId(2L)
                .presente(true)
                .fecha(LocalDateTime.now())
                .build();

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(KafkaConfig.TOPIC_ASISTENCIA, "5", dto)).thenReturn(future);

        assertDoesNotThrow(() -> producer.enviarAsistencia(dto));
        verify(kafkaTemplate).send(eq(KafkaConfig.TOPIC_ASISTENCIA), eq("5"), eq(dto));
    }

    @Test
    void enviarAsistencia_ManejaErrorAsincronoSinLanzarExcepcion() {
        AsistenciaEventoDto dto = AsistenciaEventoDto.builder()
                .asistenciaId(11L)
                .alumnoId(7L)
                .cursoId(3L)
                .presente(false)
                .build();

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Broker caido"));
        when(kafkaTemplate.send(KafkaConfig.TOPIC_ASISTENCIA, "7", dto)).thenReturn(future);

        assertDoesNotThrow(() -> producer.enviarAsistencia(dto));
    }
}

