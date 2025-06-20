package org.ncapas.canchitas.utils.mappers;

import org.ncapas.canchitas.entities.Jornada;
import org.ncapas.canchitas.DTOs.response.JornadaResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class JornadaMapper {

    public static JornadaResponseDTO toDTO(Jornada entidad) {
        return JornadaResponseDTO.builder()
                .idJornada(entidad.getIdJornada())
                .semana(entidad.getSemana().getDia().toString())
                .horaInicio(entidad.getHoraInicio())   // LocalTime directo
                .horaFin(entidad.getHoraFin())
                .precioPorHora(entidad.getPrecioPorHora())
                .estadoDisponibilidad(entidad.getEstadoDisponibilidad().getEstado().toString())
                .build();
    }


    public static List<JornadaResponseDTO> toDTOList(List<Jornada> entidades) {
        return entidades.stream()
                .map(JornadaMapper::toDTO)
                .collect(Collectors.toList());
    }
}