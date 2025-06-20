package org.ncapas.canchitas;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.entities.Semana;
import org.ncapas.canchitas.repositories.SemanaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SemanaSeeder implements CommandLineRunner {

    private final SemanaRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            for (Semana.Dia dia : Semana.Dia.values()) {
                repo.save(Semana.builder().dia(dia).build());
            }
            System.out.println("Semanas precargadas ✓");
        }
    }
}
