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

    private final ReservaRepository              reservaRepo;
    private final UsuarioRepostitory             usuarioRepo;
    private final LugarRepository                lugarRepo;
    private final MetodoPagoRepository           metodoRepo;
    private final CanchaRepository               canchaRepo;
    private final EstadoDisponibilidadRepository estadoDispRepo;  // ①

    /* ─────────────────── LECTURA ─────────────────── */

    @Override
    public List<ReservaResponseDTO> findAll() {
        return ReservaMapper.toDTOList(reservaRepo.findAll());
    }

    @Override
    public ReservaResponseDTO findById(int id) {
        return reservaRepo.findById(id)
                .map(ReservaMapper::toDTO)
                .orElseThrow(() -> new ReservaNotFoundException(
                        "Reserva no encontrada con id " + id));
    }

    /* ─────────────────── CREAR ─────────────────── */

    @Override
    @Transactional
    public ReservaResponseDTO save(ReservaRequestDTO dto) {

        //validacion de campos
        validarCamposLlenos(dto);

        // — cargar entidades relacionadas —
        Usuario usuario = usuarioRepo.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ReservaNotFoundException(
                        "Usuario no encontrado con id " + dto.getUsuarioId()));

        Lugar lugar = lugarRepo.findById(dto.getLugarId())
                .orElseThrow(() -> new ReservaNotFoundException(
                        "Lugar no encontrado con id " + dto.getLugarId()));

        MetodoPago metodo = metodoRepo.findById(dto.getMetodoPagoId())
                .orElseThrow(() -> new ReservaNotFoundException(
                        "Método de pago no encontrado con id " + dto.getMetodoPagoId()));

        Cancha cancha = canchaRepo.findById(dto.getCanchaId())
                .orElseThrow(() -> new ReservaNotFoundException(
                        "Cancha no encontrada con id " + dto.getCanchaId()));

        // — convertir la fecha a nuestro enum Semana.Dia —
        LocalDate fecha = dto.getFechaReserva();
        DayOfWeek dow   = fecha.getDayOfWeek();                          // MONDAY…SUNDAY
        final Semana.Dia diaEnum = Semana.Dia.values()[dow.getValue() - 1];

        // — localizar el bloque que cubra el horario pedido —
        Jornada bloque = cancha.getJornadas().stream()
                .filter(j -> j.getSemana().getDia() == diaEnum)
                .filter(j -> !dto.getHoraEntrada().isBefore(j.getHoraInicio())
                        && !dto.getHoraSalida().isAfter(j.getHoraFin()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe bloque disponible que cubra el horario solicitado"));

        // — calcular precioTotal = precioPorHora × horas —
        long minutos  = Duration.between(dto.getHoraEntrada(), dto.getHoraSalida()).toMinutes();
        double horas   = minutos / 60.0;
        double precio  = bloque.getPrecioPorHora() * horas;

        // — marcar como NO_DISPONIBLE todos los bloques dentro del rango —
        EstadoDisponibilidad noDisp = estadoDispRepo
                .findByEstado
                        (EstadoDisponibilidad.Status.NO_DISPONIBLE)
                .orElseThrow(() -> new IllegalStateException(
                        "Estado NO_DISPONIBLE no existe en la base de datos"));

        cancha.getJornadas().stream()
                .filter(j -> j.getSemana().getDia() == diaEnum)
                .filter(j -> !j.getHoraInicio().isBefore(dto.getHoraEntrada())
                        && !j.getHoraFin().isAfter(dto.getHoraSalida()))
                .forEach(j -> j.setEstadoDisponibilidad(noDisp));

        // — construir y guardar la reserva —
        Reserva nueva = Reserva.builder()
                .fechaReserva(java.sql.Date.valueOf(fecha))
                .horaEntrada(dto.getHoraEntrada())
                .horaSalida(dto.getHoraSalida())
                .precioTotal(precio)
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
            throw new ReservaNotFoundException(
                    "No existe reserva con id " + id);
        }
        reservaRepo.deleteById(id);
    }

    /* ─────────────────── OBTENER RESERVA POR USUARIO ─────────────────── */


    @Override
    public List<ReservaResponseDTO> findByUsuario(Integer idUsuario) {
        return ReservaMapper.toDTOList(
                reservaRepo.findByUsuario_IdUsuario(idUsuario)
        );
    }

    /* ─────────────────── OBTENER RESERVA POR USUARIO Y ESTADO ─────────────────── */

    @Override
    public List<ReservaResponseDTO> findByUsuarioAndEstado(Integer idUsuario,
                                                           Reserva.EstadoReserva estado) {
        return ReservaMapper.toDTOList(
                reservaRepo.findByUsuario_IdUsuarioAndEstadoReserva(idUsuario, estado)
        );
    }
    /* ─────────────────── OBTENER RESERVA POR FECHA ,ADMIN─────────────────── */

    @Override
    public List<ReservaResponseDTO> findAllByFechaReserva(LocalDate fechaReserva) {
        java.sql.Date sql = java.sql.Date.valueOf(fechaReserva);
        return ReservaMapper.toDTOList(reservaRepo.findByFechaReserva(sql));
    }
    /* ─────────────────── OBTENER RESERVA POR CANCHA ,ADMIN─────────────────── */

    @Override
    public List<ReservaResponseDTO> findByCanchaId(int canchaId) {
        return ReservaMapper.toDTOList(
                reservaRepo.findByCancha_IdCancha(canchaId)
        );
    }

    //validar que todos los campos esten llenos
    private void validarCamposLlenos(ReservaRequestDTO dto) {
        if (dto.getFechaReserva() == null
                || dto.getHoraEntrada() == null
                || dto.getHoraSalida() == null
                || dto.getUsuarioId() == null
                || dto.getLugarId() == null
                || dto.getMetodoPagoId() == null
                || dto.getCanchaId() == null) {
            throw new IllegalArgumentException("Todos los campos del formulario de reserva deben estar completos.");
        }
    }

}
