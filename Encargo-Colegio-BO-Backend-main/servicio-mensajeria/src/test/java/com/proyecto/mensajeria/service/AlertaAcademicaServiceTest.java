package com.proyecto.mensajeria.service;

import com.proyecto.mensajeria.dto.AsistenciaEventoDto;
import com.proyecto.mensajeria.dto.NotaEventoDto;
import com.proyecto.mensajeria.entity.Mensaje;
import com.proyecto.mensajeria.repository.MensajeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AlertaAcademicaServiceTest {

    @Mock
    private MensajeRepository mensajeRepository;

    @InjectMocks
    private AlertaAcademicaService service;

    @Test
    void procesarAlertaAsistencia_InasistenciaGuardaMensaje() {
        AsistenciaEventoDto dto = AsistenciaEventoDto.builder()
                .alumnoId(4L)
                .cursoId(1L)
                .presente(false)
                .build();

        service.procesarAlertaAsistencia(dto);
        verify(mensajeRepository).save(any(Mensaje.class));
    }

    @Test
    void procesarAlertaAsistencia_PresenteNoGuardaMensaje() {
        AsistenciaEventoDto dto = AsistenciaEventoDto.builder()
                .alumnoId(4L)
                .cursoId(1L)
                .presente(true)
                .build();

        service.procesarAlertaAsistencia(dto);
        verify(mensajeRepository, never()).save(any(Mensaje.class));
    }

    @Test
    void procesarAlertaNota_NotaRojaGuardaMensaje() {
        NotaEventoDto dto = NotaEventoDto.builder()
                .alumnoId(4L)
                .cursoId(1L)
                .asignatura("historia")
                .calificacion(new BigDecimal("3.5"))
                .build();

        service.procesarAlertaNota(dto);
        verify(mensajeRepository).save(any(Mensaje.class));
    }

    @Test
    void procesarAlertaNota_NotaAprobatoriaNoGuardaMensaje() {
        NotaEventoDto dto = NotaEventoDto.builder()
                .alumnoId(4L)
                .cursoId(1L)
                .asignatura("historia")
                .calificacion(new BigDecimal("4.5"))
                .build();

        service.procesarAlertaNota(dto);
        verify(mensajeRepository, never()).save(any(Mensaje.class));
    }
}

