// src/main/java/org/ncapas/canchitas/Controllers/ReservaController.java
package org.ncapas.canchitas.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.DTOs.request.ReservaRequestDTO;
import org.ncapas.canchitas.DTOs.response.ReservaResponseDTO;
import org.ncapas.canchitas.Service.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    /* ───────────────────────────────
       1) Listar reservas (ADMIN)
    ─────────────────────────────── */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<ReservaResponseDTO> listar() {
        return reservaService.findAll();
    }

    /* ───────────────────────────────
       2) Detalle por ID (cualquier rol autenticado)
    ─────────────────────────────── */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> detalle(@PathVariable int id) {
        return ResponseEntity.ok(reservaService.findById(id));
    }

    /* ───────────────────────────────
       3) Crear reserva (ROLE_USER)
    ─────────────────────────────── */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(
            @Valid @RequestBody ReservaRequestDTO dto) {

        ReservaResponseDTO creada = reservaService.save(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getIdReserva())
                .toUri();

        return ResponseEntity.created(location).body(creada);
    }

    /* ───────────────────────────────
       4) Eliminar reserva (ADMIN)
    ─────────────────────────────── */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable int id) {

        // obtener info antes de borrar (para mensaje)
        ReservaResponseDTO r = reservaService.findById(id);
        reservaService.delete(id);

        String msg = "La reserva #" + r.getIdReserva() + " de "
                + r.getNombreUsuario() + " para la cancha "
                + r.getNombreCancha() + " ha sido eliminada";

        return ResponseEntity.ok( Map.of("mensaje", msg) );
    }
}
