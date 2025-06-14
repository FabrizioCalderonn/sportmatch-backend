package org.ncapas.canchitas.DTOs.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.ncapas.canchitas.entities.Semana;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemanaRequestDTO {

    /**
     * Día de la semana.
     * Debe venir uno de: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO
     * esto tambien se tiene que trabajar en el controlador
     */

    @NotNull(message = "El día de la semana es obligatorio")
    private Semana.Dia dia;
}
