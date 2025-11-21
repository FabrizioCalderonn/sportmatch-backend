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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository              reservaRepo;
    private final UsuarioRepostitory             usuarioRepo;
    private final LugarRepository                lugarRepo;
    private final MetodoPagoRepository           metodoRepo;
    private final CanchaRepository               canchaRepo;
    private final EstadoDisponibilidadRepository estadoDispRepo;

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

    @Override
    @Transactional
    public ReservaResponseDTO save(ReservaRequestDTO dto) {
        System.out.println("Iniciando proceso de reserva - Fecha: " + dto.getFechaReserva() +
                           ", Usuario ID: " + dto.getUsuarioId() + ", Cancha ID: " + dto.getCanchaId());

        validarCamposLlenos(dto);

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

        double precioTotal = calcularPrecioDesdeJornadas(dto, cancha);

        try {
            marcarJornadasNoDisponibles(dto, cancha);
        } catch (Exception e) {
            System.err.println("Error al actualizar disponibilidad de jornadas: " + e.getMessage());
        }

        Reserva nueva = Reserva.builder()
                .fechaReserva(java.sql.Date.valueOf(dto.getFechaReserva()))
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

        Reserva guardada = reservaRepo.save(nueva);
        System.out.println("Reserva creada exitosamente - ID: " + guardada.getIdReserva() +
                           ", Total: $" + precioTotal);

        return ReservaMapper.toDTO(guardada);
    }

    /**
     * Calcula el precio total basándose en las jornadas configuradas.
     * Si no encuentra jornadas, usa un precio por defecto.
     */
    private double calcularPrecioDesdeJornadas(ReservaRequestDTO dto, Cancha cancha) {
        // Calcular duración en horas
        long minutos = Duration.between(dto.getHoraEntrada(), dto.getHoraSalida()).toMinutes();
        double horas = minutos / 60.0;

        System.out.println("Calculando precio - Duracion: " + minutos + " minutos (" + horas + " horas)");

        // Validar que la cancha tenga jornadas configuradas
        if (cancha.getJornadas() == null || cancha.getJornadas().isEmpty()) {
            double precioDefecto = 10.0;
            System.out.println("Cancha sin jornadas configuradas. Usando precio por defecto: $" + precioDefecto + "/hora");
            return precioDefecto * horas;
        }

        // Obtener día de la semana
        LocalDate fecha = dto.getFechaReserva();
        DayOfWeek dow = fecha.getDayOfWeek();
        Semana.Dia diaEnum = Semana.Dia.values()[dow.getValue() - 1];

        System.out.println("Buscando jornada para: " + diaEnum + " " + dto.getHoraEntrada() + "-" + dto.getHoraSalida());

        // Buscar jornada que cubra el horario solicitado
        Optional<Jornada> jornadaOpt = cancha.getJornadas().stream()
                .filter(j -> j.getSemana() != null && j.getSemana().getDia() == diaEnum)
                .filter(j -> !dto.getHoraEntrada().isBefore(j.getHoraInicio())
                        && !dto.getHoraSalida().isAfter(j.getHoraFin()))
                .findFirst();

        double precioPorHora;
        if (jornadaOpt.isPresent()) {
            Jornada jornada = jornadaOpt.get();
            precioPorHora = jornada.getPrecioPorHora();
            System.out.println("Jornada encontrada - Precio/hora: $" + precioPorHora);
        } else {
            precioPorHora = 10.0; // Precio por defecto
            System.out.println("Jornada no encontrada. Usando precio por defecto: $" + precioPorHora + "/hora");
        }

        double precioTotal = precioPorHora * horas;
        System.out.println("Precio total calculado: $" + precioTotal);

        return precioTotal;
    }

    /**
     * Marca las jornadas como NO_DISPONIBLE para el día reservado.
     */
    private void marcarJornadasNoDisponibles(ReservaRequestDTO dto, Cancha cancha) {
        // Validar que la cancha tenga jornadas
        if (cancha.getJornadas() == null || cancha.getJornadas().isEmpty()) {
            System.out.println("Cancha sin jornadas configuradas - no se actualiza disponibilidad");
            return;
        }

        // Buscar estado NO_DISPONIBLE
        Optional<EstadoDisponibilidad> noDispOpt = estadoDispRepo
                .findByEstado(EstadoDisponibilidad.Status.NO_DISPONIBLE);

        if (noDispOpt.isEmpty()) {
            System.out.println("Estado NO_DISPONIBLE no existe en BD - saltando actualizacion");
            return;
        }

        EstadoDisponibilidad noDisp = noDispOpt.get();
        LocalDate fecha = dto.getFechaReserva();
        DayOfWeek dow = fecha.getDayOfWeek();
        Semana.Dia diaEnum = Semana.Dia.values()[dow.getValue() - 1];

        // Marcar jornadas como no disponibles
        long jornadasActualizadas = cancha.getJornadas().stream()
                .filter(j -> j.getSemana() != null && j.getSemana().getDia() == diaEnum)
                .filter(j -> !j.getHoraInicio().isBefore(dto.getHoraEntrada())
                        && !j.getHoraFin().isAfter(dto.getHoraSalida()))
                .peek(j -> j.setEstadoDisponibilidad(noDisp))
                .count();

        System.out.println("Jornadas marcadas como NO_DISPONIBLE: " + jornadasActualizadas);
    }

    @Override
    public void delete(int id) {
        if (!reservaRepo.existsById(id)) {
            throw new ReservaNotFoundException("No existe reserva con id " + id);
        }
        reservaRepo.deleteById(id);
        System.out.println("Reserva eliminada - ID: " + id);
    }

    @Override
    public List<ReservaResponseDTO> findByUsuario(Integer idUsuario) {
        List<Reserva> reservas = reservaRepo.findByUsuario_IdUsuario(idUsuario);
        System.out.println("Reservas encontradas para usuario " + idUsuario + ": " + reservas.size());
        return ReservaMapper.toDTOList(reservas);
    }

    @Override
    public List<ReservaResponseDTO> findByUsuarioAndEstado(Integer idUsuario,
                                                           Reserva.EstadoReserva estado) {
        return ReservaMapper.toDTOList(
                reservaRepo.findByUsuario_IdUsuarioAndEstadoReserva(idUsuario, estado)
        );
    }

    @Override
    public List<ReservaResponseDTO> findAllByFechaReserva(LocalDate fechaReserva) {
        java.sql.Date sql = java.sql.Date.valueOf(fechaReserva);
        return ReservaMapper.toDTOList(reservaRepo.findByFechaReserva(sql));
    }

    @Override
    public List<ReservaResponseDTO> findByCanchaId(int canchaId) {
        return ReservaMapper.toDTOList(
                reservaRepo.findByCancha_IdCancha(canchaId)
        );
    }

    @Override
    public List<String> findFechasOcupadasByCancha(Integer canchaId) {
        return reservaRepo.findByCancha_IdCancha(canchaId).stream()
                .map(reserva -> {
                    java.sql.Date sqlDate = (java.sql.Date) reserva.getFechaReserva();
                    return sqlDate.toLocalDate().toString();
                })
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findHorasOcupadasByCanchaAndFecha(Integer canchaId, LocalDate fecha) {
        java.sql.Date sqlDate = java.sql.Date.valueOf(fecha);
        
        return reservaRepo.findByCancha_IdCanchaAndFechaReserva(canchaId, sqlDate).stream()
                .map(reserva -> reserva.getHoraEntrada().toString())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private void validarCamposLlenos(ReservaRequestDTO dto) {
        if (dto.getFechaReserva() == null) {
            throw new IllegalArgumentException("La fecha de reserva es obligatoria");
        }
        if (dto.getHoraEntrada() == null) {
            throw new IllegalArgumentException("La hora de entrada es obligatoria");
        }
        if (dto.getHoraSalida() == null) {
            throw new IllegalArgumentException("La hora de salida es obligatoria");
        }
        if (dto.getUsuarioId() == null) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        if (dto.getLugarId() == null) {
            throw new IllegalArgumentException("El lugar es obligatorio");
        }
        if (dto.getMetodoPagoId() == null) {
            throw new IllegalArgumentException("El método de pago es obligatorio");
        }
        if (dto.getCanchaId() == null) {
            throw new IllegalArgumentException("La cancha es obligatoria");
        }
    }
}