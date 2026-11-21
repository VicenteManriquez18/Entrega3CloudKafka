package com.proyecto.reportes.service;

import com.proyecto.reportes.dto.AsistenciaEventoDto;
import com.proyecto.reportes.dto.NotaEventoDto;
import com.proyecto.reportes.entity.ReporteAsistencia;
import com.proyecto.reportes.entity.ReporteNota;
import com.proyecto.reportes.repository.ReporteAsistenciaRepository;
import com.proyecto.reportes.repository.ReporteNotaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnaliticaEventosServiceTest {

    @Mock
    private ReporteAsistenciaRepository reporteAsistenciaRepository;

    @Mock
    private ReporteNotaRepository reporteNotaRepository;

    @InjectMocks
    private AnaliticaEventosService service;

    @Test
    void procesarAsistencia_GuardaReporteYCalculaPorcentaje() {
        AsistenciaEventoDto dto = AsistenciaEventoDto.builder()
                .asistenciaId(100L)
                .alumnoId(1L)
                .cursoId(2L)
                .presente(true)
                .fecha(LocalDateTime.now())
                .build();

        when(reporteAsistenciaRepository.porcentajeAsistenciaAlumno(1L)).thenReturn(90.0);

        service.procesarAsistencia(dto);

        verify(reporteAsistenciaRepository).save(any(ReporteAsistencia.class));
        verify(reporteAsistenciaRepository).porcentajeAsistenciaAlumno(1L);
    }

    @Test
    void procesarNota_GuardaReporteYCalculaPromedio() {
        NotaEventoDto dto = NotaEventoDto.builder()
                .notaId(200L)
                .alumnoId(1L)
                .cursoId(2L)
                .asignatura("matematica")
                .calificacion(new BigDecimal("6.0"))
                .fecha(LocalDateTime.now())
                .build();

        when(reporteNotaRepository.promedioNotasPorAlumno(1L)).thenReturn(6.0);

        service.procesarNota(dto);

        verify(reporteNotaRepository).save(any(ReporteNota.class));
        verify(reporteNotaRepository).promedioNotasPorAlumno(1L);
    }
}

