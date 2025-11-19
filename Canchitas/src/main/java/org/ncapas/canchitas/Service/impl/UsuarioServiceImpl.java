package org.ncapas.canchitas.Service.impl;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.DTOs.request.UsuarioRequestDTO;
import org.ncapas.canchitas.DTOs.response.UsuarioResponseDTO;
import org.ncapas.canchitas.entities.Usuario;
import org.ncapas.canchitas.entities.Rol;
import org.ncapas.canchitas.exception.UsuarioNotFoundException;
import org.ncapas.canchitas.repositories.RolRepository;
import org.ncapas.canchitas.repositories.UsuarioRepostitory;
import org.ncapas.canchitas.Service.UsuarioService;
import org.ncapas.canchitas.utils.mappers.UsuarioMapper;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;   // ← nuevo import

import java.util.List;


@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepostitory usuarioRepo;
    private final RolRepository      rolRepo;
    private final PasswordEncoder    passwordEncoder;   // ← inyectado por Spring

    @Override
    public List<UsuarioResponseDTO> findAll() {
        return UsuarioMapper.toDTOList(usuarioRepo.findAll());
    }

    @Override
    public UsuarioResponseDTO findById(int id) {
        Usuario u = usuarioRepo.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(
                        "Usuario no encontrado con id " + id));
        return UsuarioMapper.toDTO(u);
    }

    @Override
    public UsuarioResponseDTO save(UsuarioRequestDTO dto) {

        //validar todos los campos llenos
        validarCamposLlenos(dto);

        //verificar si el correo ya existe
        if (usuarioRepo.existsByCorreo(dto.getCorreo())){
            throw new IllegalArgumentException("Ya existe un usuario con ese correo");
        }

        // Mapea el DTO a entidad (incluye solo el id del rol)
        Usuario entidad = UsuarioMapper.toEntity(dto);

        // Encripta la contraseña
        entidad.setContrasena(passwordEncoder.encode(dto.getContrasena()));

        // Resuelve el rol real
        Rol rol = rolRepo.findById(dto.getRolId())
                .orElseThrow(() -> new UsuarioNotFoundException(
                        "Rol no encontrado con id " + dto.getRolId()));
        entidad.setRol(rol);

        // Guarda y devuelve DTO
        Usuario guardado = usuarioRepo.save(entidad);
        return UsuarioMapper.toDTO(guardado);
    }

    @Override
    public void delete(int id) {
        if (!usuarioRepo.existsById(id)) {
            throw new UsuarioNotFoundException("Usuario no existe con id " + id);
        }
        usuarioRepo.deleteById(id);
    }

    //validar todos los campos
    private void validarCamposLlenos(UsuarioRequestDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()
                || dto.getApellido() == null || dto.getApellido().isBlank()
                || dto.getCorreo() == null || dto.getCorreo().isBlank()
                || dto.getContrasena() == null || dto.getContrasena().isBlank()
                || dto.getRolId() == null) {
            throw new IllegalArgumentException("Todos los campos del formulario deben estar completos.");
        }
    }

}
