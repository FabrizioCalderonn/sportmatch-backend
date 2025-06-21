package org.ncapas.canchitas.utils.mappers;

import org.ncapas.canchitas.entities.Reserva;
import org.ncapas.canchitas.DTOs.response.ReservaResponseDTO;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ReservaMapper {

    private static final SimpleDateFormat DATE_FMT =
            new SimpleDateFormat("yyyy-MM-dd");

    // Formato 12h con sufijo y puntos: "07:00 AM" → "07:00 a. m."
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("hh:mm a", new Locale("es", "ES"));

    /** Convierte una entidad Reserva a su DTO de respuesta */
    public static ReservaResponseDTO toDTO(Reserva e) {
        return ReservaResponseDTO.builder()
                .idReserva(e.getIdReserva())
                .nombreUsuario(
                        e.getUsuario().getNombre() + " " +
                                e.getUsuario().getApellido()
                )
                .nombreCancha(e.getCancha().getNombre())
                .nombreLugar(e.getLugar().getNombre())
                .metodoPago(e.getMetodoPago().getMetodoPago().toString())
                .estadoReserva(e.getEstadoReserva().toString())
                .fechaReserva(DATE_FMT.format(e.getFechaReserva()))
                .fechaCreacion(DATE_FMT.format(e.getFechaCreacion()))
                .horaEntrada(formatTime(e.getHoraEntrada()))
                .horaSalida (formatTime(e.getHoraSalida()))
                .precioTotal(e.getPrecioTotal())
                .build();
    }

    /** Lista de Reservas → lista de DTOs */
    public static List<ReservaResponseDTO> toDTOList(List<Reserva> list) {
        return list.stream()
                .map(ReservaMapper::toDTO)
                .toList();
    }

    /** Formatea un LocalTime a “hh:mm a. m./p. m.” */
    private static String formatTime(LocalTime t) {
        // formatea en inglés con AM/PM y luego convierte a “a. m.” / “p. m.”
        String out = t.format(TIME_FMT);
        return out
                .replace("AM", "a. m.")
                .replace("PM", "p. m.");
    }
}
