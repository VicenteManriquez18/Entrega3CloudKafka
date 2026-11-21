package com.proyecto.mensajeria.messaging;

import com.proyecto.mensajeria.dto.AsistenciaEventoDto;
import com.proyecto.mensajeria.dto.NotaEventoDto;
import com.proyecto.mensajeria.service.AlertaAcademicaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlertasKafkaListenerTest {

    @Mock
    private AlertaAcademicaService alertaAcademicaService;

    @InjectMocks
    private AlertasKafkaListener listener;

    @Test
    void onAsistenciaEvento_ValidoDelegaAService() {
        AsistenciaEventoDto dto = AsistenciaEventoDto.builder()
                .asistenciaId(1L)
                .alumnoId(3L)
                .cursoId(1L)
                .presente(false)
                .build();

        listener.onAsistenciaEvento(dto);
        verify(alertaAcademicaService).procesarAlertaAsistencia(dto);
    }

    @Test
    void onAsistenciaEvento_InvalidoLanzaIllegalArgument() {
        AsistenciaEventoDto dto = AsistenciaEventoDto.builder()
                .alumnoId(3L)
                .presente(false)
                .build();

        assertThrows(IllegalArgumentException.class, () -> listener.onAsistenciaEvento(dto));
        verify(alertaAcademicaService, never()).procesarAlertaAsistencia(dto);
    }

    @Test
    void onNotaEvento_ValidoDelegaAService() {
        NotaEventoDto dto = NotaEventoDto.builder()
                .notaId(1L)
                .alumnoId(3L)
                .cursoId(1L)
                .calificacion(new BigDecimal("2.8"))
                .build();

        listener.onNotaEvento(dto);
        verify(alertaAcademicaService).procesarAlertaNota(dto);
    }

    @Test
    void onNotaEvento_MenorQueUnoLanzaIllegalArgument() {
        NotaEventoDto dto = NotaEventoDto.builder()
                .notaId(1L)
                .alumnoId(3L)
                .calificacion(new BigDecimal("0.5"))
                .build();

        assertThrows(IllegalArgumentException.class, () -> listener.onNotaEvento(dto));
        verify(alertaAcademicaService, never()).procesarAlertaNota(dto);
    }
}

