package org.ncapas.canchitas.Controllers;

import org.ncapas.canchitas.DTOs.request.UsuarioRequestDTO;
import org.ncapas.canchitas.DTOs.response.UsuarioResponseDTO;
import org.ncapas.canchitas.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuariosController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** GET /api/v1/usuarios — Listar todos los usuarios */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        List<UsuarioResponseDTO> lista = usuarioService.findAll();
        return ResponseEntity.ok(lista);
    }

    /** GET /api/v1/usuarios/{id} — Obtener un usuario por su ID */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuario(@PathVariable int id) {
        UsuarioResponseDTO dto = usuarioService.findById(id);
        return ResponseEntity.ok(dto);
    }

    /** POST /api/v1/usuarios — Crear un nuevo usuario */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(
            @Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO creada = usuarioService.save(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarUsuario(@PathVariable int id) {

        // 1) Traer primero al usuario para conocer su nombre
        UsuarioResponseDTO usuario = usuarioService.findById(id);   // lanza 404 si no existe

        // 2) Eliminar
        usuarioService.delete(id);

        // 3) Mensaje de confirmación
        String msg = "El usuario \"" + usuario.getNombre()
                + "\" (id=" + usuario.getIdUsuario() + ") ha sido eliminado";

        return ResponseEntity.ok( Map.of("mensaje", msg) );   // 200 OK
    }

}
