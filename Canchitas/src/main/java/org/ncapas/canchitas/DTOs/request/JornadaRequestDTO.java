package org.ncapas.canchitas.DTOs.request;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JornadaRequestDTO {

    @NotNull(message = "La hora de inicio es obligatoria")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFin;

    @NotNull(message = "El precio por hora es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio por hora debe ser mayor que 0")
    private Double precioPorHora;

    @NotNull(message = "Debe especificar la semana (ID)")
    private Integer semanaId;

    @NotNull(message = "Debe especificar el estado de disponibilidad (ID)")
    private Integer estadoDisponibilidadId;
}
