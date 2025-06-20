package org.ncapas.canchitas.DTOs.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class JornadaResponseDTO {
    private Integer idJornada;
    private String semana;
    private LocalTime horaInicio;   // ← era String
    private LocalTime horaFin;
    private Double precioPorHora;
    private String estadoDisponibilidad;
}
