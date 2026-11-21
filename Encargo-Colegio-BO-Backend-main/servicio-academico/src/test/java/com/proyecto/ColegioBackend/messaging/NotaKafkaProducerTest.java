package com.proyecto.ColegioBackend.messaging;

import com.proyecto.ColegioBackend.config.KafkaConfig;
import com.proyecto.ColegioBackend.dto.NotaEventoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotaKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private NotaKafkaProducer producer;

    @Test
    void enviarNota_UsaAlumnoIdYCursoIdComoKey() {
        NotaEventoDto dto = NotaEventoDto.builder()
                .notaId(15L)
                .alumnoId(5L)
                .cursoId(2L)
                .asignatura("matematica 2")
                .calificacion(new BigDecimal("6.2"))
                .fecha(LocalDateTime.now())
                .build();

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(KafkaConfig.TOPIC_NOTAS, "5-2", dto)).thenReturn(future);

        assertDoesNotThrow(() -> producer.enviarNota(dto));
        verify(kafkaTemplate).send(eq(KafkaConfig.TOPIC_NOTAS), eq("5-2"), eq(dto));
    }

    @Test
    void enviarNota_ManejaErrorAsincronoSinLanzarExcepcion() {
        NotaEventoDto dto = NotaEventoDto.builder()
                .notaId(16L)
                .alumnoId(8L)
                .cursoId(1L)
                .calificacion(new BigDecimal("3.4"))
                .build();

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka offline"));
        when(kafkaTemplate.send(KafkaConfig.TOPIC_NOTAS, "8-1", dto)).thenReturn(future);

        assertDoesNotThrow(() -> producer.enviarNota(dto));
    }
}

