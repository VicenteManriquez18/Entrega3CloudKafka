package com.proyecto.reportes.listener;

import com.proyecto.reportes.config.KafkaConfig;
import com.proyecto.reportes.dto.AsistenciaEventoDto;
import com.proyecto.reportes.dto.NotaEventoDto;
import com.proyecto.reportes.service.AnaliticaEventosService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EventosAcademicosKafkaListener {

    private static final BigDecimal NOTA_MIN = new BigDecimal("1.0");
    private static final BigDecimal NOTA_MAX = new BigDecimal("7.0");

    private final AnaliticaEventosService analiticaEventosService;

    public EventosAcademicosKafkaListener(AnaliticaEventosService analiticaEventosService) {
        this.analiticaEventosService = analiticaEventosService;
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
        analiticaEventosService.procesarAsistencia(dto);
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
        analiticaEventosService.procesarNota(dto);
    }
}

