package org.ncapas.canchitas.Service.impl;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.DTOs.request.CanchaRequestDTO;
import org.ncapas.canchitas.DTOs.request.CanchaUpdateRequestDTO;
import org.ncapas.canchitas.DTOs.response.CanchasResponseDTO;
import org.ncapas.canchitas.entities.Cancha;
import org.ncapas.canchitas.entities.TipoCancha;
import org.ncapas.canchitas.entities.Jornada;
import org.ncapas.canchitas.entities.Lugar;
import org.ncapas.canchitas.exception.CanchaNotFoundException;
import org.ncapas.canchitas.repositories.CanchaRepository;
import org.ncapas.canchitas.repositories.TipoCanchaRepository;
import org.ncapas.canchitas.repositories.JornadaRepository;
import org.ncapas.canchitas.repositories.LugarRepository;
import org.ncapas.canchitas.Service.CanchasService;
import org.ncapas.canchitas.utils.mappers.CanchaMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CanchasServiceImpl implements CanchasService {

    private final CanchaRepository canchaRepo;
    private final TipoCanchaRepository tipoCanchaRepo;
    private final JornadaRepository jornadaRepo;
    private final LugarRepository lugarRepo;

    @Override
    public List<CanchasResponseDTO> findAll() {
        List<Cancha> todas = canchaRepo.findAll();
        return CanchaMapper.toDTOList(todas);
    }

    @Override
    public CanchasResponseDTO findById(int id) {
        Cancha cancha = canchaRepo.findById(id)
                .orElseThrow(() -> new CanchaNotFoundException("Cancha no encontrada con id " + id));
        return CanchaMapper.toDTO(cancha);
    }

    @Override
    public CanchasResponseDTO save(CanchaRequestDTO dto) {
        // 1) buscar las entidades relacionadas
        TipoCancha tipo = tipoCanchaRepo.findById(dto.getTipoCanchaId())
                .orElseThrow(() -> new CanchaNotFoundException("Tipo de cancha no encontrado con id " + dto.getTipoCanchaId()));
        Jornada jornada = jornadaRepo.findById(dto.getJornadaId())
                .orElseThrow(() -> new CanchaNotFoundException("Jornada no encontrada con id " + dto.getJornadaId()));
        Lugar lugar = lugarRepo.findById(dto.getLugarId())
                .orElseThrow(() -> new CanchaNotFoundException("Lugar no encontrado con id " + dto.getLugarId()));

        // 2) mapear DTO → entidad
        Cancha nueva = Cancha.builder()
                .nombre(dto.getNombre())
                .foto(dto.getFoto())
                .numeroCancha(dto.getNumeroCancha())
                .tipoCancha(tipo)
                .jornada(jornada)
                .lugar(lugar)
                .build();

        // 3) guardar y devolver DTO
        Cancha guardada = canchaRepo.save(nueva);
        return CanchaMapper.toDTO(guardada);
    }

    @Override
    public CanchasResponseDTO update(CanchaUpdateRequestDTO dto) {
        // 1) obtener la entidad existente
        Cancha exist = canchaRepo.findById(dto.getIdCancha())
                .orElseThrow(() -> new CanchaNotFoundException("Cancha no encontrada con id " + dto.getIdCancha()));

        // 2) buscar y setear relaciones nuevas
        TipoCancha tipo = tipoCanchaRepo.findById(dto.getTipoCancha())
                .orElseThrow(() -> new CanchaNotFoundException("Tipo de cancha no encontrado con id " + dto.getTipoCancha()));
        Jornada jornada = jornadaRepo.findById(dto.getJornada())
                .orElseThrow(() -> new CanchaNotFoundException("Jornada no encontrada con id " + dto.getJornada()));
        Lugar lugar = lugarRepo.findById(dto.getLugar())
                .orElseThrow(() -> new CanchaNotFoundException("Lugar no encontrado con id " + dto.getLugar()));

        // 3) actualizar campos
        exist.setNombre(dto.getNombre());
        exist.setFoto(dto.getFoto());
        exist.setNumeroCancha(dto.getNumeroCancha());
        exist.setTipoCancha(tipo);
        exist.setJornada(jornada);
        exist.setLugar(lugar);

        // 4) guardar y devolver DTO
        Cancha actualizada = canchaRepo.save(exist);
        return CanchaMapper.toDTO(actualizada);
    }

    @Override
    public void delete(int id) {
        if (!canchaRepo.existsById(id)) {
            throw new CanchaNotFoundException("No existe cancha con id " + id);
        }
        canchaRepo.deleteById(id);
    }
}
