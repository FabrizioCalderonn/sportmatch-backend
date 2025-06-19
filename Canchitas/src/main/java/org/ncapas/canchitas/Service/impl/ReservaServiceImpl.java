package org.ncapas.canchitas.Service.impl;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.Service.ReservaService;
import org.ncapas.canchitas.DTOs.request.ReservaRequestDTO;
import org.ncapas.canchitas.DTOs.response.ReservaResponseDTO;
import org.ncapas.canchitas.entities.Reserva;
import org.ncapas.canchitas.entities.Usuario;
import org.ncapas.canchitas.entities.Lugar;
import org.ncapas.canchitas.entities.MetodoPago;
import org.ncapas.canchitas.entities.Cancha;
import org.ncapas.canchitas.exception.ReservaNotFoundException;
import org.ncapas.canchitas.repositories.ReservaRepository;
import org.ncapas.canchitas.repositories.UsuarioRepostitory;
import org.ncapas.canchitas.repositories.LugarRepository;
import org.ncapas.canchitas.repositories.MetodoPagoRepository;
import org.ncapas.canchitas.repositories.CanchaRepository;
import org.ncapas.canchitas.utils.mappers.ReservaMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepo;
    private final UsuarioRepostitory usuarioRepo;
    private final LugarRepository lugarRepo;
    private final MetodoPagoRepository metodoPagoRepo;
    private final CanchaRepository canchaRepo;

    @Override
    public List<ReservaResponseDTO> findAll() {
        return ReservaMapper.toDTOList(reservaRepo.findAll());
    }

    @Override
    public ReservaResponseDTO findById(int id) {
        Reserva r = reservaRepo.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException("Reserva no encontrada con id " + id));
        return ReservaMapper.toDTO(r);
    }

    @Override
    public ReservaResponseDTO save(ReservaRequestDTO dto) {
        // 1) Cargar todas las entidades relacionadas
        Usuario usuario = usuarioRepo.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ReservaNotFoundException("Usuario no encontrado con id " + dto.getUsuarioId()));
        Lugar lugar = lugarRepo.findById(dto.getLugarId())
                .orElseThrow(() -> new ReservaNotFoundException("Lugar no encontrado con id " + dto.getLugarId()));
        MetodoPago metodo = metodoPagoRepo.findById(dto.getMetodoPagoId())
                .orElseThrow(() -> new ReservaNotFoundException("Método de pago no encontrado con id " + dto.getMetodoPagoId()));
        Cancha cancha = canchaRepo.findById(dto.getCanchaId())
                .orElseThrow(() -> new ReservaNotFoundException("Cancha no encontrada con id " + dto.getCanchaId()));

        // 2) Convertir LocalDate → java.sql.Date
        Date fechaReserva = java.sql.Date.valueOf(dto.getFechaReserva());
        // y marcar fecha de creación
        Date fechaCreacion = new Date();

        // 3) Calcular precioTotal = precioPorHora * duración en horas
        Duration dur = Duration.between(dto.getHoraEntrada(), dto.getHoraSalida());
        double horas = dur.toMinutes() / 60.0;
        double precioPorHora = cancha.getJornada().getPrecioPorHora();
        double precioTotal = horas * precioPorHora;

        // 4) Construir la entidad Reserva
        Reserva nueva = Reserva.builder()
                .fechaReserva(fechaReserva)
                .horaEntrada(dto.getHoraEntrada())
                .horaSalida(dto.getHoraSalida())
                .precioTotal(precioTotal)
                .fechaCreacion(fechaCreacion)
                // Asumimos nueva reserva PENDIENTE
                .estadoReserva(Reserva.EstadoReserva.PENDIENTE)
                .usuario(usuario)
                .lugar(lugar)
                .metodoPago(metodo)
                .cancha(cancha)
                .build();

        // 5) Guardar y devolver DTO
        Reserva guardada = reservaRepo.save(nueva);
        return ReservaMapper.toDTO(guardada);
    }

    @Override
    public void delete(int id) {
        if (!reservaRepo.existsById(id)) {
            throw new ReservaNotFoundException("No existe reserva con id " + id);
        }
        reservaRepo.deleteById(id);
    }
}
