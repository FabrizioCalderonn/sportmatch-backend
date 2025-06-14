package org.ncapas.canchitas.DTOs.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CanchaRequestDTO {

    @NotBlank(message = "El nombre de la cancha no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La URL de la foto no puede estar vacía")
    private String foto;

    @NotNull(message = "El número de cancha es obligatorio")
    @Min(value = 1, message = "El número de cancha debe ser al menos 1")
    private Integer numeroCancha;

    @NotNull(message = "Debe especificar el tipo de cancha (ID)")
    private Integer tipoCanchaId;

    @NotNull(message = "Debe especificar la jornada (ID)")
    private Integer jornadaId;

    @NotNull(message = "Debe especificar el lugar (ID)")
    private Integer lugarId;
}