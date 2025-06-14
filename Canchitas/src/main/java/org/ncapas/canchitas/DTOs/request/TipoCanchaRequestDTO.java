package org.ncapas.canchitas.DTOs.request;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import org.ncapas.canchitas.entities.TipoCancha.Tipo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoCanchaRequestDTO {

    /**
     * Tipo de cancha. Valores válidos:
     * FUTBOL_RAPIDO, GRAMA_ARTIFICIAL(aunque a este le llaman futbol 11)
     */

    @NotNull(message = "El tipo de cancha es obligatorio")
    private Tipo tipo;
}
