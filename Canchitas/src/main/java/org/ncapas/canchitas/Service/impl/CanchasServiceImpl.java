package org.ncapas.canchitas.Service.impl;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.DTOs.request.*;
import org.ncapas.canchitas.DTOs.response.CanchasResponseDTO;
import org.ncapas.canchitas.entities.*;
import org.ncapas.canchitas.exception.CanchaNotFoundException;
import org.ncapas.canchitas.repositories.*;
import org.ncapas.canchitas.Service.CanchasService;
import org.ncapas.canchitas.utils.mappers.CanchaMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CanchasServiceImpl implements CanchasService {

    private final CanchaRepository canchaRepo;
    private final TipoCanchaRepository tipoRepo;
    private final LugarRepository lugarRepo;
    private final SemanaRepository semanaRepo;
    private final EstadoDisponibilidadRepository estadoRepo;

    // ---------- Lectura ----------
    @Override public List<CanchasResponseDTO> findAll() {
        return CanchaMapper.toDTOList(canchaRepo.findAll());
    }

    @Override public CanchasResponseDTO findById(int id) {
        return canchaRepo.findById(id)
                .map(CanchaMapper::toDTO)
                .orElseThrow(() -> new CanchaNotFoundException("Cancha no encontrada con id " + id));
    }

    // ---------- Crear ----------
    @Override
    @Transactional
    public CanchasResponseDTO save(CanchaRequestDTO dto) {

        // Evitar mismo número de cancha en mismo lugar
        if (canchaRepo.existsByLugar_IdLugarAndNumeroCancha(dto.getLugarId(), dto.getNumeroCancha())) {
            throw new IllegalArgumentException("Ya existe una cancha con ese número en el lugar indicado");
        }

        TipoCancha tipo   = tipoRepo.findById(dto.getTipoCanchaId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de cancha no encontrado"));
        Lugar lugar       = lugarRepo.findById(dto.getLugarId())
                .orElseThrow(() -> new IllegalArgumentException("Lugar no encontrado"));

        // --- Construir entidad Cancha ---
        Cancha cancha = Cancha.builder()
                .nombre(dto.getNombre())
                .numeroCancha(dto.getNumeroCancha())
                .tipoCancha(tipo)
                .lugar(lugar)
                .imagenes(new ArrayList<>(dto.getImagenes()))
                .build();

        // --- Mapear y validar jornadas ---
        cancha.setJornadas(crearJornadasDesdeDTO(dto.getJornadas(), cancha));

        // Persiste en cascada
        Cancha saved = canchaRepo.save(cancha);
        return CanchaMapper.toDTO(saved);
    }

    // ---------- Actualizar ----------
    @Override
    @Transactional
    public CanchasResponseDTO update(CanchaUpdateRequestDTO dto) {

        Cancha cancha = canchaRepo.findById(dto.getIdCancha())
                .orElseThrow(() -> new CanchaNotFoundException("Cancha no encontrada con id " + dto.getIdCancha()));

        TipoCancha tipo = tipoRepo.findById(dto.getTipoCanchaId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de cancha no encontrado"));
        Lugar lugar     = lugarRepo.findById(dto.getLugarId())
                .orElseThrow(() -> new IllegalArgumentException("Lugar no encontrado"));

        cancha.setNombre(dto.getNombre());
        cancha.setNumeroCancha(dto.getNumeroCancha());
        cancha.setTipoCancha(tipo);
        cancha.setLugar(lugar);
        cancha.getImagenes().clear();
        cancha.getImagenes().addAll(dto.getImagenes());

        // Reemplazar jornadas (orphanRemoval=true se encarga de borrar viejas)
        cancha.getJornadas().clear();
        cancha.getJornadas().addAll(crearJornadasDesdeDTO(dto.getJornadas(), cancha));

        return CanchaMapper.toDTO(canchaRepo.save(cancha));
    }

    // ---------- Eliminar ----------
    @Override public void delete(int id) {
        if (!canchaRepo.existsById(id)) {
            throw new CanchaNotFoundException("No existe cancha con id " + id);
        }
        canchaRepo.deleteById(id);
    }

    // ---------- Helpers ----------
    /* ---------- Helpers ---------- */
    private List<Jornada> crearJornadasDesdeDTO(List<JornadaRequestDTO> listaDTO,
                                                Cancha cancha) {

        Map<Integer, List<Intervalo>> mapSolape = new HashMap<>();
        List<Jornada> resultado = new ArrayList<>();

        for (JornadaRequestDTO dto : listaDTO) {

            // 1) Cargar día y estado
            Semana semana = semanaRepo.findById(dto.getSemanaId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Semana no encontrada con id " + dto.getSemanaId()));

            EstadoDisponibilidad estado = estadoRepo.findById(dto.getEstadoDisponibilidadId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Estado disponibilidad no encontrada con id " + dto.getEstadoDisponibilidadId()));

            // 2) Recorrer en bloques de 60 min
            java.time.LocalTime cursor = dto.getHoraInicio();
            while (cursor.isBefore(dto.getHoraFin())) {

                java.time.LocalTime fin = cursor.plusHours(1);
                if (fin.isAfter(dto.getHoraFin())) break;           // descarta tramo < 1 h

                // Validar solapamiento dentro del mismo día
                Intervalo nuevo = new Intervalo(cursor, fin);
                List<Intervalo> existentes = mapSolape
                        .computeIfAbsent(semana.getIdSemana(), k -> new ArrayList<>());

                if (existentes.stream().anyMatch(nuevo::solapaCon)) {
                    throw new IllegalArgumentException(
                            "Bloque solapado en " + semana.getDia() + " a las " + cursor);
                }
                existentes.add(nuevo);

                // 3) Crear sub-jornada de 1 h
                Jornada j = Jornada.builder()
                        .horaInicio(cursor)
                        .horaFin(fin)
                        .precioPorHora(dto.getPrecioPorHora())   // Double
                        .semana(semana)
                        .estadoDisponibilidad(estado)
                        .cancha(cancha)
                        .build();

                resultado.add(j);
                cursor = fin;                                     // avanza una hora
            }
        }
        return resultado;
    }


    /** Pequeña clase auxiliar para detectar solapamientos */
    @RequiredArgsConstructor
    private static class Intervalo {
        private final java.time.LocalTime ini;
        private final java.time.LocalTime fin;
        boolean solapaCon(Intervalo o) {
            return !(fin.isBefore(o.ini) || ini.isAfter(o.fin) || fin.equals(o.ini) || ini.equals(o.fin));
        }
    }
}
