package org.ncapas.canchitas.Service;

import org.ncapas.canchitas.DTOs.request.CanchaRequestDTO;
import org.ncapas.canchitas.DTOs.request.CanchaUpdateRequestDTO;
import org.ncapas.canchitas.DTOs.response.CanchasResponseDTO;
import org.ncapas.canchitas.DTOs.response.JornadaResponseDTO;
import org.ncapas.canchitas.entities.Semana;

import java.util.List;


public interface CanchasService {
    List<CanchasResponseDTO> findAll();
    CanchasResponseDTO findById(int id);
    CanchasResponseDTO save(CanchaRequestDTO cancha);
    CanchasResponseDTO update(CanchaUpdateRequestDTO cancha);
    void delete(int id);
    List<JornadaResponseDTO> findJornadasByCanchaAndDia(int canchaId,
                                                        Semana.Dia dia);
}



