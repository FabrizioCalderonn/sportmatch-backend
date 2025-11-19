package org.ncapas.canchitas.DTOs.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequestDTO {

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "Debe proporcionar un correo válido")
    private String correo;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, max = 15, message = "La contraseña debe tener entre 6 y 15 caracteres")
    @Pattern(regexp = "^[^';]*$", message = "La contraseña no puede contener comillas ni punto y coma")
    private String contrasena;
}
