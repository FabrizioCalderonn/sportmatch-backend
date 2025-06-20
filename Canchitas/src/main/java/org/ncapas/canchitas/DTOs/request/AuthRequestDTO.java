package org.ncapas.canchitas.DTOs.request;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String correo;
    private String contrasena;
}
