package org.ncapas.canchitas.Service.impl;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.DTOs.request.LugarRequestDTO;
import org.ncapas.canchitas.DTOs.response.LugarResponseDTO;
import org.ncapas.canchitas.entities.Lugar;
import org.ncapas.canchitas.entities.Zona;
import org.ncapas.canchitas.exception.LugarNotFoundException;
import org.ncapas.canchitas.repositories.LugarRepository;
import org.ncapas.canchitas.repositories.ZonaRepository;
import org.ncapas.canchitas.Service.LugarService;
import org.ncapas.canchitas.utils.mappers.LugarMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LugarServiceImpl implements LugarService {

    private final LugarRepository lugarRepo;
    private final ZonaRepository zonaRepo;

    @Override
    public List<LugarResponseDTO> findAll() {
        return LugarMapper.toDTOList(lugarRepo.findAll());
    }

    @Override
    public LugarResponseDTO findById(int id) {
        Lugar lugar = lugarRepo.findById(id)
                .orElseThrow(() -> new LugarNotFoundException("Lugar no encontrado con id " + id));
        return LugarMapper.toDTO(lugar);
    }

    @Override
    public LugarResponseDTO save(LugarRequestDTO dto) {
        // 1) Cargar la zona relacionada
        Zona zona = zonaRepo.findById(dto.getZona())
                .orElseThrow(() -> new LugarNotFoundException("Zona no encontrada con id " + dto.getZona()));

        // 2) Mapear DTO → entidad
        Lugar nueva = Lugar.builder()
                .nombre(dto.getNombre())
                .direccion(dto.getDireccion())
                .capacidad(dto.getCapacidad())
                .zona(zona)
                .build();

        // 3) Guardar y devolver DTO
        Lugar guardada = lugarRepo.save(nueva);
        return LugarMapper.toDTO(guardada);
    }

    @Override
    public void delete(int id) {
        if (!lugarRepo.existsById(id)) {
            throw new LugarNotFoundException("No existe lugar con id " + id);
        }
        lugarRepo.deleteById(id);
    }
}
