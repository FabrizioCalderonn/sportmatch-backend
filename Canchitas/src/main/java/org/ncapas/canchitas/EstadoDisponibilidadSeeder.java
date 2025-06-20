package org.ncapas.canchitas;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.entities.EstadoDisponibilidad;
import org.ncapas.canchitas.repositories.EstadoDisponibilidadRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstadoDisponibilidadSeeder implements CommandLineRunner {

    private final EstadoDisponibilidadRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            repo.save(EstadoDisponibilidad.builder()
                    .estado(EstadoDisponibilidad.Status.DISPONIBLE)
                    .build());
            repo.save(EstadoDisponibilidad.builder()
                    .estado(EstadoDisponibilidad.Status.NO_DISPONIBLE)
                    .build());
            System.out.println("Estados de disponibilidad precargados ✓");
        }
    }
}
