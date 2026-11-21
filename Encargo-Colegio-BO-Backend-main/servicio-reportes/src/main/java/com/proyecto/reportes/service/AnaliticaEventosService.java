package com.proyecto.reportes.service;

import com.proyecto.reportes.dto.AsistenciaEventoDto;
import com.proyecto.reportes.dto.NotaEventoDto;
import com.proyecto.reportes.entity.ReporteAsistencia;
import com.proyecto.reportes.entity.ReporteNota;
import com.proyecto.reportes.repository.ReporteAsistenciaRepository;
import com.proyecto.reportes.repository.ReporteNotaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AnaliticaEventosService {

    private static final Logger log = LoggerFactory.getLogger(AnaliticaEventosService.class);

    private final ReporteAsistenciaRepository reporteAsistenciaRepository;
    private final ReporteNotaRepository reporteNotaRepository;

    public AnaliticaEventosService(ReporteAsistenciaRepository reporteAsistenciaRepository,
                                   ReporteNotaRepository reporteNotaRepository) {
        this.reporteAsistenciaRepository = reporteAsistenciaRepository;
        this.reporteNotaRepository = reporteNotaRepository;
    }

    @Transactional
    public void procesarAsistencia(AsistenciaEventoDto dto) {
        LocalDateTime ahora = LocalDateTime.now();
        ReporteAsistencia reporte = new ReporteAsistencia(
                dto.getAsistenciaId() != null ? dto.getAsistenciaId() : 0L,
                dto.getAlumnoId(),
                dto.getCursoId(),
                dto.getPresente(),
                ahora,
                dto.getFecha() != null ? dto.getFecha() : ahora
        );
        reporteAsistenciaRepository.save(reporte);

        Double porcentaje = reporteAsistenciaRepository.porcentajeAsistenciaAlumno(dto.getAlumnoId());
        log.info("[KAFKA ANALITICA REPORTES] Asistencia procesada alumnoId={} cursoId={} presente={} porcentajeAsistencia={}%",
                dto.getAlumnoId(),
                dto.getCursoId(),
                dto.getPresente(),
                porcentaje != null ? String.format("%.1f", porcentaje) : "N/A");
    }

    @Transactional
    public void procesarNota(NotaEventoDto dto) {
        LocalDateTime ahora = LocalDateTime.now();
        ReporteNota reporte = new ReporteNota(
                dto.getNotaId() != null ? dto.getNotaId() : 0L,
                dto.getCursoId() != null ? dto.getCursoId() : 0L,
                dto.getAlumnoId(),
                dto.getCalificacion().doubleValue(),
                ahora,
                dto.getFecha() != null ? dto.getFecha() : ahora
        );
        reporteNotaRepository.save(reporte);

        Double promedio = reporteNotaRepository.promedioNotasPorAlumno(dto.getAlumnoId());
        log.info("[KAFKA ANALITICA REPORTES] Nota procesada alumnoId={} cursoId={} asignatura={} calificacion={} promedioActual={}",
                dto.getAlumnoId(),
                dto.getCursoId(),
                dto.getAsignatura(),
                dto.getCalificacion(),
                promedio != null ? String.format("%.2f", promedio) : "N/A");
    }
}

