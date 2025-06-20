package org.ncapas.canchitas.DTOs.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CanchasResponseDTO {
    private Integer idCancha;
    private String nombre;
    private List<String> imagenes;
    private Integer numeroCancha;
    private String tipoCancha;
    private List<JornadaResponseDTO> jornadas;
    private String lugar;
}
