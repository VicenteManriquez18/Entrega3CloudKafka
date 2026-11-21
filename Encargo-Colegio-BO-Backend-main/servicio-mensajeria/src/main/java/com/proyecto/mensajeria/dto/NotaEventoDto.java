package com.proyecto.mensajeria.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaEventoDto {

    private Long notaId;
    private Long alumnoId;
    private Long cursoId;
    private String asignatura;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 7, message = "La calificación máxima es 7")
    private BigDecimal calificacion;

    private LocalDateTime fecha;
}

