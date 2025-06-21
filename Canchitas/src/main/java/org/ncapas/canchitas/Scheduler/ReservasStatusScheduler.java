package org.ncapas.canchitas.scheduler;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.entities.Reserva;
import org.ncapas.canchitas.entities.Reserva.EstadoReserva;
import org.ncapas.canchitas.repositories.ReservaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservasStatusScheduler {

    private final ReservaRepository reservaRepo;

    /**
     * Cada minuto (cron: "segundos minutos horas días...").
     * Ajusta si lo quieres cada 5 min, etc.
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void marcarFinalizadas() {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();

        // 1) Traer todas las pendientes
        List<Reserva> pendientes = reservaRepo.findByEstadoReserva(EstadoReserva.PENDIENTE);

        // 2) Filtrar las que ya expiraron
        List<Reserva> aFinalizar = pendientes.stream()
                .filter(r -> {
                    LocalDate resFecha = new java.sql.Date(r.getFechaReserva().getTime())
                            .toLocalDate();
                    // si la fecha es anterior a hoy
                    if (resFecha.isBefore(hoy)) return true;
                    // si es hoy y la hora de salida ya pasó
                    return resFecha.isEqual(hoy) && r.getHoraSalida().isBefore(ahora);
                })
                .toList();

        // 3) Marcar y guardar
        if (!aFinalizar.isEmpty()) {
            aFinalizar.forEach(r -> r.setEstadoReserva(EstadoReserva.FINALIZADA));
            reservaRepo.saveAll(aFinalizar);
        }
    }
}
