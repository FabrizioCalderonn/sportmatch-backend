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
        System.out.println("========================================");
        System.out.println("🎯 INICIANDO PROCESO DE RESERVA");
        System.out.println("========================================");
        System.out.println("📥 Datos recibidos:");
        System.out.println("   - Fecha: " + dto.getFechaReserva());
        System.out.println("   - Hora entrada: " + dto.getHoraEntrada());
        System.out.println("   - Hora salida: " + dto.getHoraSalida());
        System.out.println("   - Usuario ID: " + dto.getUsuarioId());
        System.out.println("   - Lugar ID: " + dto.getLugarId());
        System.out.println("   - Cancha ID: " + dto.getCanchaId());
        System.out.println("   - Método Pago ID: " + dto.getMetodoPagoId());
        System.out.println("========================================");

        // 1. VALIDAR CAMPOS
        validarCamposLlenos(dto);
        System.out.println("✅ Validación de campos: OK");

        // 2. BUSCAR ENTIDADES
        Usuario usuario = usuarioRepo.findById(dto.getUsuarioId())
                .orElseThrow(() -> {
                    System.out.println("❌ ERROR: Usuario no encontrado con ID " + dto.getUsuarioId());
                    return new ReservaNotFoundException(
                            "Usuario no encontrado con id " + dto.getUsuarioId());
                });
        System.out.println("✅ Usuario encontrado: " + usuario.getNombre());

        Lugar lugar = lugarRepo.findById(dto.getLugarId())
                .orElseThrow(() -> {
                    System.out.println("❌ ERROR: Lugar no encontrado con ID " + dto.getLugarId());
                    return new ReservaNotFoundException(
                            "Lugar no encontrado con id " + dto.getLugarId());
                });
        System.out.println("✅ Lugar encontrado: " + lugar.getNombre());

        MetodoPago metodo = metodoRepo.findById(dto.getMetodoPagoId())
                .orElseThrow(() -> {
                    System.out.println("❌ ERROR: Método de pago no encontrado con ID " + dto.getMetodoPagoId());
                    return new ReservaNotFoundException(
                            "Método de pago no encontrado con id " + dto.getMetodoPagoId());
                });
        System.out.println("✅ Método de pago encontrado: " + metodo.getNombre());

        Cancha cancha = canchaRepo.findById(dto.getCanchaId())
                .orElseThrow(() -> {
                    System.out.println("❌ ERROR: Cancha no encontrada con ID " + dto.getCanchaId());
                    return new ReservaNotFoundException(
                            "Cancha no encontrada con id " + dto.getCanchaId());
                });
        System.out.println("✅ Cancha encontrada: " + cancha.getNombre());

        // 3. CALCULAR PRECIO DESDE JORNADAS
        double precioTotal = calcularPrecioDesdeJornadas(dto, cancha);
        System.out.println("💰 Precio total calculado: $" + precioTotal);

        // 4. MARCAR JORNADAS COMO NO DISPONIBLES (si existen)
        try {
            marcarJornadasNoDisponibles(dto, cancha);
        } catch (Exception e) {
            System.out.println("⚠️ Advertencia al actualizar disponibilidad: " + e.getMessage());
            // Continuar de todas formas
        }

        // 5. CREAR Y GUARDAR RESERVA
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

        System.out.println("📝 Guardando reserva en la base de datos...");
        Reserva guardada = reservaRepo.save(nueva);

        System.out.println("========================================");
        System.out.println("✅ ¡RESERVA GUARDADA EXITOSAMENTE!");
        System.out.println("   - ID Reserva: " + guardada.getIdReserva());
        System.out.println("   - Usuario ID: " + usuario.getIdUsuario());
        System.out.println("   - Usuario: " + usuario.getNombre());
        System.out.println("   - Cancha: " + cancha.getNombre());
        System.out.println("   - Fecha: " + dto.getFechaReserva());
        System.out.println("   - Horario: " + dto.getHoraEntrada() + " - " + dto.getHoraSalida());
        System.out.println("   - Total: $" + precioTotal);
        System.out.println("========================================");

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

        System.out.println("========================================");
        System.out.println("💰 CÁLCULO DE PRECIO:");
        System.out.println("   - Duración: " + minutos + " minutos (" + horas + " horas)");

        // Obtener día de la semana
        LocalDate fecha = dto.getFechaReserva();
        DayOfWeek dow = fecha.getDayOfWeek();
        Semana.Dia diaEnum = Semana.Dia.values()[dow.getValue() - 1];

        System.out.println("   - Día de la semana: " + diaEnum);
        System.out.println("   - Buscando jornada para este día y horario...");

        // Buscar jornada que cubra el horario solicitado
        Optional<Jornada> jornadaOpt = cancha.getJornadas().stream()
                .filter(j -> j.getSemana().getDia() == diaEnum)
                .filter(j -> !dto.getHoraEntrada().isBefore(j.getHoraInicio())
                        && !dto.getHoraSalida().isAfter(j.getHoraFin()))
                .findFirst();

        double precioPorHora;
        if (jornadaOpt.isPresent()) {
            Jornada jornada = jornadaOpt.get();
            precioPorHora = jornada.getPrecioPorHora();
            System.out.println("   ✅ Jornada encontrada!");
            System.out.println("   - Horario jornada: " + jornada.getHoraInicio() + " - " + jornada.getHoraFin());
            System.out.println("   - Precio/hora de jornada: $" + precioPorHora);
        } else {
            precioPorHora = 10.0; // Precio por defecto
            System.out.println("   ⚠️ No se encontró jornada específica");
            System.out.println("   - Usando precio por defecto: $" + precioPorHora + "/hora");
        }

        double precioTotal = precioPorHora * horas;
        System.out.println("   - PRECIO TOTAL: $" + precioTotal);
        System.out.println("========================================");

        return precioTotal;
    }

    /**
     * Marca las jornadas como NO_DISPONIBLE para el día reservado.
     */
    private void marcarJornadasNoDisponibles(ReservaRequestDTO dto, Cancha cancha) {
        // Buscar estado NO_DISPONIBLE
        Optional<EstadoDisponibilidad> noDispOpt = estadoDispRepo
                .findByEstado(EstadoDisponibilidad.Status.NO_DISPONIBLE);

        if (noDispOpt.isEmpty()) {
            System.out.println("⚠️ Estado NO_DISPONIBLE no existe en BD, saltando actualización");
            return;
        }

        EstadoDisponibilidad noDisp = noDispOpt.get();
        LocalDate fecha = dto.getFechaReserva();
        DayOfWeek dow = fecha.getDayOfWeek();
        Semana.Dia diaEnum = Semana.Dia.values()[dow.getValue() - 1];

        // Marcar jornadas como no disponibles
        long jornadasActualizadas = cancha.getJornadas().stream()
                .filter(j -> j.getSemana().getDia() == diaEnum)
                .filter(j -> !j.getHoraInicio().isBefore(dto.getHoraEntrada())
                        && !j.getHoraFin().isAfter(dto.getHoraSalida()))
                .peek(j -> j.setEstadoDisponibilidad(noDisp))
                .count();

        System.out.println("📅 Jornadas marcadas como NO_DISPONIBLE: " + jornadasActualizadas);
    }

    @Override
    public void delete(int id) {
        if (!reservaRepo.existsById(id)) {
            throw new ReservaNotFoundException("No existe reserva con id " + id);
        }
        reservaRepo.deleteById(id);
        System.out.println("🗑️ Reserva eliminada con ID: " + id);
    }

    @Override
    public List<ReservaResponseDTO> findByUsuario(Integer idUsuario) {
        System.out.println("🔍 Buscando reservas del usuario ID: " + idUsuario);
        List<Reserva> reservas = reservaRepo.findByUsuario_IdUsuario(idUsuario);
        System.out.println("📋 Reservas encontradas: " + reservas.size());
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