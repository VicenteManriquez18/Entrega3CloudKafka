package com.proyecto.mensajeria.service;

import com.proyecto.mensajeria.dto.AsistenciaEventoDto;
import com.proyecto.mensajeria.dto.NotaEventoDto;
import com.proyecto.mensajeria.entity.Mensaje;
import com.proyecto.mensajeria.repository.MensajeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AlertaAcademicaService {

    private static final Logger log = LoggerFactory.getLogger(AlertaAcademicaService.class);
    private static final BigDecimal NOTA_APROBATORIA = new BigDecimal("4.0");

    private final MensajeRepository mensajeRepository;

    public AlertaAcademicaService(MensajeRepository mensajeRepository) {
        this.mensajeRepository = mensajeRepository;
    }

    @Transactional
    public void procesarAlertaAsistencia(AsistenciaEventoDto dto) {
        if (Boolean.FALSE.equals(dto.getPresente())) {
            String contenido = String.format(
                    "Alerta automática: se registró inasistencia para el alumno ID %d en el curso ID %d.",
                    dto.getAlumnoId(), dto.getCursoId());
            guardarAlerta(dto.getAlumnoId(), contenido);
            log.info("[KAFKA ALERTA ACADEMICA] Alerta de inasistencia guardada para alumnoId={} cursoId={}",
                    dto.getAlumnoId(), dto.getCursoId());
        } else {
            log.info("[KAFKA ALERTA ACADEMICA] Asistencia presente para alumnoId={} cursoId={}, no requiere alerta.",
                    dto.getAlumnoId(), dto.getCursoId());
        }
    }

    @Transactional
    public void procesarAlertaNota(NotaEventoDto dto) {
        if (dto.getCalificacion().compareTo(NOTA_APROBATORIA) < 0) {
            String contenido = String.format(
                    "Alerta automática: calificación insuficiente (%s) en asignatura %s (curso ID %d) para alumno ID %d.",
                    dto.getCalificacion(),
                    dto.getAsignatura() != null ? dto.getAsignatura() : "N/A",
                    dto.getCursoId() != null ? dto.getCursoId() : 0L,
                    dto.getAlumnoId());
            guardarAlerta(dto.getAlumnoId(), contenido);
            log.info("[KAFKA ALERTA ACADEMICA] Alerta de nota baja ({}) guardada para alumnoId={} asignatura={}",
                    dto.getCalificacion(), dto.getAlumnoId(), dto.getAsignatura());
        } else {
            log.info("[KAFKA ALERTA ACADEMICA] Nota aprobatoria ({}) para alumnoId={}, no requiere alerta.",
                    dto.getCalificacion(), dto.getAlumnoId());
        }
    }

    private void guardarAlerta(Long alumnoId, String contenido) {
        Mensaje alerta = Mensaje.builder()
                .remitenteId(0L)
                .remitenteNombre("Sistema Alerta Académica")
                .remitenteRol("SISTEMA")
                .destinatarioId(alumnoId)
                .destinatarioNombre("Alumno #" + alumnoId)
                .destinatarioRol("ALUMNO")
                .contenido(contenido)
                .fechaEnvio(LocalDateTime.now())
                .build();
        mensajeRepository.save(alerta);
    }
}

