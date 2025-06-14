package org.ncapas.canchitas.DTOs.request;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import org.ncapas.canchitas.entities.EstadoDisponibilidad.Status;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoDisponibilidadRequestDTO {

    /**
     * Estado de disponibilidad.
     * Valores válidos: DISPONIBLE, NO_DISPONIBLE
     */
    @NotNull(message = "El estado es obligatorio")
    private Status estado;
}
