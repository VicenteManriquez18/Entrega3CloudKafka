package com.proyecto.reportes.listener;

import com.proyecto.reportes.dto.AsistenciaEventoDto;
import com.proyecto.reportes.dto.NotaEventoDto;
import com.proyecto.reportes.service.AnaliticaEventosService;
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
class EventosAcademicosKafkaListenerTest {

    @Mock
    private AnaliticaEventosService analiticaEventosService;

    @InjectMocks
    private EventosAcademicosKafkaListener listener;

    @Test
    void onAsistenciaEvento_ValidoDelegaAService() {
        AsistenciaEventoDto dto = AsistenciaEventoDto.builder()
                .asistenciaId(1L)
                .alumnoId(10L)
                .cursoId(2L)
                .presente(true)
                .build();

        listener.onAsistenciaEvento(dto);
        verify(analiticaEventosService).procesarAsistencia(dto);
    }

    @Test
    void onAsistenciaEvento_SinAlumnoIdLanzaIllegalArgument() {
        AsistenciaEventoDto dto = AsistenciaEventoDto.builder()
                .cursoId(2L)
                .presente(true)
                .build();

        assertThrows(IllegalArgumentException.class, () -> listener.onAsistenciaEvento(dto));
        verify(analiticaEventosService, never()).procesarAsistencia(dto);
    }

    @Test
    void onNotaEvento_ValidoDelegaAService() {
        NotaEventoDto dto = NotaEventoDto.builder()
                .notaId(1L)
                .alumnoId(10L)
                .cursoId(2L)
                .calificacion(new BigDecimal("5.5"))
                .build();

        listener.onNotaEvento(dto);
        verify(analiticaEventosService).procesarNota(dto);
    }

    @Test
    void onNotaEvento_CalificacionFueraDeRangoLanzaIllegalArgument() {
        NotaEventoDto dto = NotaEventoDto.builder()
                .notaId(2L)
                .alumnoId(10L)
                .cursoId(2L)
                .calificacion(new BigDecimal("8.5"))
                .build();

        assertThrows(IllegalArgumentException.class, () -> listener.onNotaEvento(dto));
        verify(analiticaEventosService, never()).procesarNota(dto);
    }
}

