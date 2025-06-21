package org.ncapas.canchitas.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.DTOs.request.LugarRequestDTO;
import org.ncapas.canchitas.DTOs.response.LugarResponseDTO;
import org.ncapas.canchitas.DTOs.response.ZonaComboDTO;
import org.ncapas.canchitas.Service.LugarService;
import org.ncapas.canchitas.entities.Zona;
import org.ncapas.canchitas.repositories.ZonaRepository;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lugares")
@RequiredArgsConstructor
public class LugarController {

    private final LugarService   lugarService;
    private final ZonaRepository zonaRepo;

    /* ---------- ZONAS (combo) ---------- */

    /** Lista de zonas – público */
    @GetMapping("/zonas")
    public List<ZonaComboDTO> listarZonas() {
        return zonaRepo.findAll().stream()
                .map(z -> new ZonaComboDTO(z.getIdZona(),
                        z.getDepartamento() + " – " + z.getDistrito()))
                .toList();
    }

    /* ---------- CRUD LUGARES ---------- */

    /** Listar lugares – requiere JWT (cualquier rol) */
    @GetMapping
    public List<LugarResponseDTO> listarLugares() {
        return lugarService.findAll();
    }

    /** Obtener lugar por ID – requiere JWT */
    @GetMapping("/{id}")
    public ResponseEntity<LugarResponseDTO> obtenerLugar(@PathVariable int id) {
        return ResponseEntity.ok(lugarService.findById(id));
    }

    /** Crear lugar – SOLO rol ADMIN */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<LugarResponseDTO> crearLugar(
            @Valid @RequestBody LugarRequestDTO dto) {

        LugarResponseDTO creado = lugarService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /** Eliminar lugar – SOLO rol ADMIN */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLugar(@PathVariable int id) {
        lugarService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
