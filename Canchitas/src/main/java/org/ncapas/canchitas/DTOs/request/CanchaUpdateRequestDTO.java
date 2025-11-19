package org.ncapas.canchitas.DTOs.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "El nombre de la cancha no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9\\sáéíóúÁÉÍÓÚñÑ.,-]*$", message = "El nombre no puede contener caracteres especiales no permitidos")
    private String nombre;

    @NotEmpty(message = "Debe proporcionar al menos una imagen")
    private List<@NotBlank(message = "La URL de la imagen no puede estar vacía")
    @Size(max = 255, message = "La URL de la imagen no puede exceder 255 caracteres")
    @Pattern(regexp = "^(http|https)://.*$", message = "La URL debe comenzar con http o https")
            String> imagenes;

    @NotNull(message = "El número de cancha es obligatorio")
    @Min(value = 1, message = "El número de cancha debe ser mayor que cero")
    private Integer numeroCancha;

    @NotNull(message = "Debe seleccionar un tipo de cancha")
    private Integer tipoCanchaId;

    @NotNull(message = "Debe seleccionar un lugar")
    private Integer lugarId;

    @NotEmpty(message = "Debe agregar al menos una jornada")
    @Valid
    private List<JornadaRequestDTO> jornadas;
}
