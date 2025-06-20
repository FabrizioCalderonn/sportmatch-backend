package org.ncapas.canchitas.Service.impl;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.Service.ReservaService;
import org.ncapas.canchitas.DTOs.request.ReservaRequestDTO;
import org.ncapas.canchitas.DTOs.response.ReservaResponseDTO;
import org.ncapas.canchitas.entities.*;
import org.ncapas.canchitas.exception.ReservaNotFoundException;
import org.ncapas.canchitas.repositories.*;
import org.ncapas.canchitas.utils.mappers.ReservaMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository    reservaRepo;
    private final UsuarioRepostitory   usuarioRepo;
    private final LugarRepository      lugarRepo;
    private final MetodoPagoRepository metodoRepo;
    private final CanchaRepository     canchaRepo;

    /* ─────────────────── LECTURA ─────────────────── */

    @Override
    public List<ReservaResponseDTO> findAll() {
        return ReservaMapper.toDTOList(reservaRepo.findAll());
    }

    @Override
    public ReservaResponseDTO findById(int id) {
        return reservaRepo.findById(id)
                .map(ReservaMapper::toDTO)
                .orElseThrow(() -> new ReservaNotFoundException("Reserva no encontrada con id " + id));
    }

    /* ─────────────────── CREAR ─────────────────── */

    @Override
    @Transactional
    public ReservaResponseDTO save(ReservaRequestDTO dto) {

        /* --- cargar entidades relacionadas --- */
        Usuario usuario = usuarioRepo.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ReservaNotFoundException("Usuario no encontrado con id " + dto.getUsuarioId()));

        Lugar lugar = lugarRepo.findById(dto.getLugarId())
                .orElseThrow(() -> new ReservaNotFoundException("Lugar no encontrado con id " + dto.getLugarId()));

        MetodoPago metodo = metodoRepo.findById(dto.getMetodoPagoId())
                .orElseThrow(() -> new ReservaNotFoundException("Método de pago no encontrado con id " + dto.getMetodoPagoId()));

        Cancha cancha = canchaRepo.findById(dto.getCanchaId())
                .orElseThrow(() -> new ReservaNotFoundException("Cancha no encontrada con id " + dto.getCanchaId()));

        /* --- localizar bloque que cubra el horario --- */
        LocalDate fecha = dto.getFechaReserva();
        DayOfWeek dow   = fecha.getDayOfWeek();

        Jornada bloque = cancha.getJornadas().stream()
                .filter(j -> j.getSemana().getDia().name().equalsIgnoreCase(dow.name()))
                .filter(j -> !dto.getHoraEntrada().isBefore(j.getHoraInicio())
                        && !dto.getHoraSalida().isAfter(j.getHoraFin()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe bloque disponible que cubra el horario solicitado"));

        /* --- calcular precioTotal = precioPorHora × horas --- */
        long minutos = Duration.between(dto.getHoraEntrada(), dto.getHoraSalida()).toMinutes();
        double horas = minutos / 60.0;                     // admite múltiplos de 1 h
        double precioTotal = bloque.getPrecioPorHora() * horas;

        /* --- construir y guardar --- */
        Reserva nueva = Reserva.builder()
                .fechaReserva(java.sql.Date.valueOf(fecha))
                .horaEntrada(dto.getHoraEntrada())
                .horaSalida(dto.getHoraSalida())
                .precioTotal(precioTotal)
                .fechaCreacion(new Date())
                .estadoReserva(Reserva.EstadoReserva.PENDIENTE)
                .usuario(usuario)
                .lugar(lugar)
                .metodoPago(metodo)
                .cancha(cancha)
                .build();

        return ReservaMapper.toDTO(reservaRepo.save(nueva));
    }

    /* ─────────────────── BORRAR ─────────────────── */

    @Override
    public void delete(int id) {
        if (!reservaRepo.existsById(id)) {
            throw new ReservaNotFoundException("No existe reserva con id " + id);
        }
        reservaRepo.deleteById(id);
    }
}
