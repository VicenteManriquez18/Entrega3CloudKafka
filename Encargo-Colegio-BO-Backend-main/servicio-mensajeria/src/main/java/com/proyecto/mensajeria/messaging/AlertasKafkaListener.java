package com.proyecto.mensajeria.messaging;

import com.proyecto.mensajeria.config.KafkaConfig;
import com.proyecto.mensajeria.dto.AsistenciaEventoDto;
import com.proyecto.mensajeria.dto.NotaEventoDto;
import com.proyecto.mensajeria.service.AlertaAcademicaService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AlertasKafkaListener {

    private static final BigDecimal NOTA_MIN = new BigDecimal("1.0");
    private static final BigDecimal NOTA_MAX = new BigDecimal("7.0");

    private final AlertaAcademicaService alertaAcademicaService;

    public AlertasKafkaListener(AlertaAcademicaService alertaAcademicaService) {
        this.alertaAcademicaService = alertaAcademicaService;
    }

    @KafkaListener(
            topics = KafkaConfig.TOPIC_ASISTENCIA,
            groupId = KafkaConfig.GROUP_ID,
            containerFactory = "asistenciaKafkaListenerContainerFactory"
    )
    public void onAsistenciaEvento(AsistenciaEventoDto dto) {
        if (dto == null || dto.getAlumnoId() == null || dto.getCursoId() == null || dto.getPresente() == null) {
            throw new IllegalArgumentException("AsistenciaEventoDto inválido: alumnoId, cursoId y presente son obligatorios");
        }
        alertaAcademicaService.procesarAlertaAsistencia(dto);
    }

    @KafkaListener(
            topics = KafkaConfig.TOPIC_NOTAS,
            groupId = KafkaConfig.GROUP_ID,
            containerFactory = "notaKafkaListenerContainerFactory"
    )
    public void onNotaEvento(NotaEventoDto dto) {
        if (dto == null || dto.getAlumnoId() == null || dto.getCalificacion() == null
                || dto.getCalificacion().compareTo(NOTA_MIN) < 0
                || dto.getCalificacion().compareTo(NOTA_MAX) > 0) {
            throw new IllegalArgumentException("NotaEventoDto inválido: alumnoId obligatorio y calificación entre 1.0 y 7.0");
        }
        alertaAcademicaService.procesarAlertaNota(dto);
    }
}

