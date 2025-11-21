package org.ncapas.canchitas.Service;

import org.ncapas.canchitas.DTOs.request.UsuarioRequestDTO;
import org.ncapas.canchitas.DTOs.response.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {
    List<UsuarioResponseDTO> findAll();
    UsuarioResponseDTO findById(int id);
    UsuarioResponseDTO save(UsuarioRequestDTO usuario);
    void delete(int id);
    UsuarioResponseDTO findByCorreo(String correo);
}
