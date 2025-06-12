package org.ncapas.canchitas.DTOs.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponseDTO {
    private Integer idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private String rol;
}
