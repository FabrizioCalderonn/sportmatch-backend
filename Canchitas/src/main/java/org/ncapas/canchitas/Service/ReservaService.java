package org.ncapas.canchitas.Service;

import org.ncapas.canchitas.DTOs.request.ReservaRequestDTO;
import org.ncapas.canchitas.DTOs.response.ReservaResponseDTO;
import org.ncapas.canchitas.entities.Reserva;

import java.time.LocalDate;
import java.util.List;


public interface ReservaService {
    List<ReservaResponseDTO> findAll();
    ReservaResponseDTO findById(int id);
    ReservaResponseDTO save(ReservaRequestDTO reserva);
    void delete(int id);
    List<ReservaResponseDTO> findByUsuario(Integer idUsuario);
    List<ReservaResponseDTO> findByUsuarioAndEstado(Integer idUsuario, Reserva.EstadoReserva estado);
    List<ReservaResponseDTO> findAllByFechaReserva(LocalDate fechaReserva);

}
