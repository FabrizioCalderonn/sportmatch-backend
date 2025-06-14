package org.ncapas.canchitas.DTOs.request;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoRequestDTO {

    /**
     * Este campo mapeará al enum MetodoPago.Metodo
     * Debe venir uno de los valores: TARJETA_DEBITO, TARJETA_CREDITO
     * Aca, se tendra que tener en cuenta los controladores para poder validar esto
     */

    @NotNull(message = "Debe especificar un método de pago")
    private Metodo metodoPago;

    public enum Metodo {
        TARJETA_DEBITO,
        TARJETA_CREDITO
    }
}
