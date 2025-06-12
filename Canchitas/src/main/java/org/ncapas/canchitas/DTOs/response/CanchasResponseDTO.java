package org.ncapas.canchitas.DTOs.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CanchasResponseDTO {
    private Integer idCancha;
    private String nombre;
    private String foto;
    private Integer numeroCancha;
    private String tipoCancha;
    private String jornada;
    private String lugar;
}
