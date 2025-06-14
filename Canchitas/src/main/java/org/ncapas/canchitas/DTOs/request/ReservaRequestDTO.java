package org.ncapas.canchitas.DTOs.request;

import lombok.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    /** Fecha de la reserva (solo fecha) */
    @NotNull(message = "La fecha de reserva es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaReserva;

    /** Hora de entrada (solo hora) */
    @NotNull(message = "La hora de entrada es obligatoria")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaEntrada;

    /** Hora de salida (solo hora) */
    @NotNull(message = "La hora de salida es obligatoria")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaSalida;

    /** ID del usuario que reserva */
    @NotNull(message = "El usuario (ID) es obligatorio")
    private Integer usuarioId;

    /** ID del lugar reservado */
    @NotNull(message = "El lugar (ID) es obligatorio")
    private Integer lugarId;

    /** ID del metodo de pago seleccionado */
    @NotNull(message = "El método de pago (ID) es obligatorio")
    private Integer metodoPagoId;

    /** ID de la cancha reservada */
    @NotNull(message = "La cancha (ID) es obligatoria")
    private Integer canchaId;
}
