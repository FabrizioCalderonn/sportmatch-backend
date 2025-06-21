// src/main/java/org/ncapas/canchitas/Controllers/ReservaController.java
package org.ncapas.canchitas.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.DTOs.request.ReservaRequestDTO;
import org.ncapas.canchitas.DTOs.response.ReservaResponseDTO;
import org.ncapas.canchitas.Service.ReservaService;
import org.ncapas.canchitas.entities.Reserva;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
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
    public List<ReservaResponseDTO> listar(
            @RequestParam(name="fechaReserva", required=false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaReserva
    ) {
        if (fechaReserva == null) {
            return reservaService.findAll();
        } else {
            return reservaService.findAllByFechaReserva(fechaReserva);
        }
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
    @PreAuthorize("hasRole('CLIENTE')")
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
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
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


    /* ───────────────────────────────
       4) Listar reservas de un usuario, OPCIONALMENTE filtradas por estado
         GET /api/reservas/usuario/{usuarioId}?estado=PENDIENTE
    ─────────────────────────────── */
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponseDTO>> reservasPorUsuario(
            @PathVariable Integer usuarioId,
            @RequestParam(required = false) Reserva.EstadoReserva estado) {

        List<ReservaResponseDTO> lista;
        if (estado != null) {
            lista = reservaService.findByUsuarioAndEstado(usuarioId, estado);
        } else {
            lista = reservaService.findByUsuario(usuarioId);
        }
        return ResponseEntity.ok(lista);
    }
}
