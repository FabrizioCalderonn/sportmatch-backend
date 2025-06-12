package org.ncapas.canchitas.DTOs.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LugarResponseDTO {
    private Integer idLugar;
    private String nombre;
    private String direccion;
    private Integer capacidad;
    private String zona;

}
