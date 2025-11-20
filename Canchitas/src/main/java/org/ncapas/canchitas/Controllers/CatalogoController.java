// Controllers/CatalogoController.java
package org.ncapas.canchitas.Controllers;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.DTOs.response.*;
import org.ncapas.canchitas.entities.*;
import org.ncapas.canchitas.repositories.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")      // prefijo común
@RequiredArgsConstructor
public class CatalogoController {

    private final LugarRepository   lugarRepo;
    private final CanchaRepository  canchaRepo;
    private final ZonaRepository    zonaRepo;      // por si quieres validar existencia
    private final MetodoPagoRepository metodoPagoRepo;

    /* ────────────────────────────────
     * 1) LUGARES por zona
     *    GET /api/zonas/{idZona}/lugares
     * ──────────────────────────────── */
    @GetMapping("/zonas/{idZona}/lugares")
    public ResponseEntity<List<LugarComboDTO>> lugaresPorZona(
            @PathVariable int idZona) {

        // (opcional) 404 si la zona no existe
        if (!zonaRepo.existsById(idZona)) {
            return ResponseEntity.notFound().build();
        }

        List<LugarComboDTO> lista = lugarRepo.findByZona_IdZona(idZona).stream()
                .map(l -> new LugarComboDTO(l.getIdLugar(), l.getNombre()))
                .toList();

        return ResponseEntity.ok(lista);
    }

    /* ────────────────────────────────
     * 2) CANCHAS por lugar
     *    GET /api/lugares/{idLugar}/canchas
     * ──────────────────────────────── */
    @GetMapping("/lugares/{idLugar}/canchas")
    public ResponseEntity<List<CanchaComboDTO>> canchasPorLugar(
            @PathVariable int idLugar) {

        List<CanchaComboDTO> lista = canchaRepo.findByLugar_IdLugar(idLugar).stream()
                .map(c -> new CanchaComboDTO(
                        c.getIdCancha(),
                        c.getNombre(),
                        c.getImagenes(),
                        c.getTipoCancha().getTipo().name()
                ))
                .toList();

        return ResponseEntity.ok(lista);
    }

    /* ────────────────────────────────
     * 3) LISTA DE ZONAS
     *    GET /api/zonas
     * ──────────────────────────────── */
    @GetMapping("/zonas")
    public ResponseEntity<List<ZonaComboDTO>> listarZonas() {
        List<ZonaComboDTO> lista = zonaRepo.findAll().stream()
                .map(z -> new ZonaComboDTO(
                        z.getIdZona(),
                        z.getDepartamento() + " - " + z.getDistrito()
                ))
                .toList();
        return ResponseEntity.ok(lista);
    }

    /* ────────────────────────────────
     * 4) METODOS DE PAGO
     *    GET /api/metodos-pago
     * ──────────────────────────────── */
    @GetMapping("/metodos-pago")
    public ResponseEntity<List<MetodoPagoDTO>> listarMetodosPago() {
        List<MetodoPagoDTO> lista = metodoPagoRepo.findAll().stream()
                .map(mp -> new MetodoPagoDTO(
                        mp.getIdMetodoPago(),
                        formatearMetodo(mp.getMetodoPago())
                ))
                .toList();
        return ResponseEntity.ok(lista);
    }

    /* ────────────────────────────────
     * Metodo auxiliar para formatear metodo de pago
     * ──────────────────────────────── */
    private String formatearMetodo(MetodoPago.Metodo metodo) {
        return switch (metodo) {
            case TARJETA_CREDITO -> "Tarjeta de Crédito";
            case TARJETA_DEBITO -> "Tarjeta de Débito";
        };
    }

}
