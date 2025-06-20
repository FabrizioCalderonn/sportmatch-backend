package org.ncapas.canchitas.DTOs.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CanchaRequestDTO {

    @NotBlank(message = "El nombre de la cancha no puede estar vacío")
    private String nombre;

    @NotEmpty(message = "Debe proporcionar al menos una imagen")
    private List<@NotBlank String> imagenes;

    @NotNull(message = "El número de cancha es obligatorio")
    @Min(value = 1, message = "El número de cancha debe ser al menos 1")
    private Integer numeroCancha;

    @NotNull(message = "Debe especificar el tipo de cancha (ID)")
    private Integer tipoCanchaId;

    @NotNull(message = "Debe especificar el lugar (ID)")
    private Integer lugarId;

    @NotEmpty(message = "Debe registrar al menos una jornada")
    @Valid
    private List<JornadaRequestDTO> jornadas;
}