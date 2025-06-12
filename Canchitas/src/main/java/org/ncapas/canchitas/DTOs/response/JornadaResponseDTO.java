package org.ncapas.canchitas.DTOs.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JornadaResponseDTO {
    private Integer idJornada;
    private String semana;
    private String horaInicio;
    private String horaFin;
    private Double precioPorHora;
    private String estadoDisponibilidad;
}
