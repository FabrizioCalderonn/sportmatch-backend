package org.ncapas.canchitas.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.DTOs.request.*;
import org.ncapas.canchitas.DTOs.response.*;
import org.ncapas.canchitas.Service.CanchasService;
import org.ncapas.canchitas.entities.TipoCancha;
import org.ncapas.canchitas.repositories.TipoCanchaRepository;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/canchas")
@RequiredArgsConstructor
public class CanchaController {

    private final CanchasService       canchasService;
    private final TipoCanchaRepository tipoRepo;

    /* ------------------------------------------------------------------
     * 1) Combo de tipos de cancha (público) ─ GET /api/canchas/tipos
     * ------------------------------------------------------------------ */
    @GetMapping("/tipos")
    public List<TipoCanchaComboDTO> listarTipos() {
        return tipoRepo.findAll().stream()
                .map(this::mapTipo)         // método auxiliar abajo
                .toList();
    }

    /* ------------------------------------------------------------------
     * 2) Crear cancha (ADMIN) ─ POST /api/canchas
     * ------------------------------------------------------------------ */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CanchasResponseDTO> crearCancha(
            @Valid @RequestBody CanchaRequestDTO dto) {

        CanchasResponseDTO creada = canchasService.save(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getIdCancha())
                .toUri();

        return ResponseEntity.created(location).body(creada);
    }

    /* ------------------------------------------------------------------
     * 3) Listar todas (cualquier rol autenticado) ─ GET /api/canchas
     * ------------------------------------------------------------------ */
    @GetMapping
    public List<CanchasResponseDTO> listarCanchas() {
        return canchasService.findAll();
    }

    /* ------------------------------------------------------------------
     * 4) Detalle por ID ─ GET /api/canchas/{id}
     * ------------------------------------------------------------------ */
    @GetMapping("/{id}")
    public ResponseEntity<CanchasResponseDTO> detalle(@PathVariable int id) {
        return ResponseEntity.ok(canchasService.findById(id));
    }

    /* ------------------------------------------------------------------
     * 5) Actualizar (ADMIN) ─ PUT /api/canchas/{id}
     * ------------------------------------------------------------------ */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CanchasResponseDTO> actualizar(
            @PathVariable int id,
            @Valid @RequestBody CanchaUpdateRequestDTO dto) {

        dto.setIdCancha(id);                      // coherencia path/body
        return ResponseEntity.ok(canchasService.update(dto));
    }

    /* ------------------------------------------------------------------
     * 6) Eliminar (ADMIN) ─ DELETE /api/canchas/{id}
     * ------------------------------------------------------------------ */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        canchasService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* ------------------------------------------------------------------
     * AUXILIAR: convertir TipoCancha a combo DTO
     * ------------------------------------------------------------------ */
    private TipoCanchaComboDTO mapTipo(TipoCancha t) {
        return new TipoCanchaComboDTO(
                t.getIdTipoCancha(),
                t.getTipo().name()   // usa .toValue() si tu enum lo expone
        );
    }
}
