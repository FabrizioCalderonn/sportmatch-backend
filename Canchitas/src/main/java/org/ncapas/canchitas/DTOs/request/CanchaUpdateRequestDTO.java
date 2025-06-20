package org.ncapas.canchitas.DTOs.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CanchaUpdateRequestDTO {

    private Integer idCancha;

    @NotBlank
    private String nombre;

    @NotEmpty
    private List<@NotBlank String> imagenes;

    @NotNull @Min(1)
    private Integer numeroCancha;

    @NotNull
    private Integer tipoCanchaId;

    @NotNull
    private Integer lugarId;

    @NotEmpty @Valid
    private List<JornadaRequestDTO> jornadas;
}