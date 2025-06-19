package org.ncapas.canchitas.Service.impl;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.DTOs.request.JornadaRequestDTO;
import org.ncapas.canchitas.DTOs.response.JornadaResponseDTO;
import org.ncapas.canchitas.entities.Jornada;
import org.ncapas.canchitas.entities.Semana;
import org.ncapas.canchitas.entities.EstadoDisponibilidad;
import org.ncapas.canchitas.exception.JornadaNotFoundException;
import org.ncapas.canchitas.repositories.JornadaRepository;
import org.ncapas.canchitas.repositories.SemanaRepository;
import org.ncapas.canchitas.repositories.EstadoDisponibilidadRepository;
import org.ncapas.canchitas.Service.JornadaService;
import org.ncapas.canchitas.utils.mappers.JornadaMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JornadaServiceImpl implements JornadaService {

    private final JornadaRepository jornadaRepo;
    private final SemanaRepository semanaRepo;
    private final EstadoDisponibilidadRepository estadoDispRepo;

    @Override
    public List<JornadaResponseDTO> findAll() {
        List<Jornada> todas = jornadaRepo.findAll();
        return JornadaMapper.toDTOList(todas);
    }

    @Override
    public JornadaResponseDTO findById(int id) {
        Jornada j = jornadaRepo.findById(id)
                .orElseThrow(() -> new JornadaNotFoundException("Jornada no encontrada con id " + id));
        return JornadaMapper.toDTO(j);
    }

    @Override
    public JornadaResponseDTO save(JornadaRequestDTO dto) {
        // 1) Cargar entidades relacionadas
        Semana semana = semanaRepo.findById(dto.getSemanaId())
                .orElseThrow(() -> new JornadaNotFoundException("Semana no encontrada con id " + dto.getSemanaId()));
        EstadoDisponibilidad estado = estadoDispRepo.findById(dto.getEstadoDisponibilidadId())
                .orElseThrow(() -> new JornadaNotFoundException("Estado de disponibilidad no encontrado con id " + dto.getEstadoDisponibilidadId()));

        // 2) Mapear DTO → entidad
        Jornada nueva = Jornada.builder()
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .precioPorHora(dto.getPrecioPorHora())
                .semana(semana)
                .estadoDisponibilidad(estado)
                .build();

        // 3) Guardar y mapear a DTO
        Jornada guardada = jornadaRepo.save(nueva);
        return JornadaMapper.toDTO(guardada);
    }

    @Override
    public void delete(int id) {
        if (!jornadaRepo.existsById(id)) {
            throw new JornadaNotFoundException("No existe jornada con id " + id);
        }
        jornadaRepo.deleteById(id);
    }
}
