package org.ncapas.canchitas.utils.mappers;

import org.ncapas.canchitas.entities.Cancha;
import org.ncapas.canchitas.DTOs.response.CanchasResponseDTO;

import java.util.List;
import java.util.stream.Collectors;


public class CanchaMapper {

    public static CanchasResponseDTO toDTO(Cancha cancha) {
        return CanchasResponseDTO.builder()
                .idCancha(cancha.getIdCancha())
                .nombre(cancha.getNombre())
                .imagenes(cancha.getImagenes())
                .numeroCancha(cancha.getNumeroCancha())
                .tipoCancha(cancha.getTipoCancha().getTipo().name())
                .lugar(cancha.getLugar().getNombre())
                .jornadas(JornadaMapper.toDTOList(cancha.getJornadas()))
                .build();
    }

    public static List<CanchasResponseDTO> toDTOList(List<Cancha> entidades) {
        return entidades.stream()
                .map(CanchaMapper::toDTO)
                .collect(Collectors.toList());
    }
}