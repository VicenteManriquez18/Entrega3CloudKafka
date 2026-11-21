package com.proyecto.mensajeria.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsistenciaEventoDto {

    private Long asistenciaId;

    @NotNull(message = "El alumnoId es obligatorio")
    private Long alumnoId;

    @NotNull(message = "El cursoId es obligatorio")
    private Long cursoId;

    private LocalDateTime fecha;

    @NotNull(message = "El campo presente es obligatorio")
    private Boolean presente;

    private String observacion;
}

