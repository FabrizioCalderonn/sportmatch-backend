// src/main/java/org/ncapas/canchitas/bootstrap/TipoCanchaSeeder.java
package org.ncapas.canchitas.bootstrap;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.entities.TipoCancha;
import org.ncapas.canchitas.entities.TipoCancha.Tipo;   // enum interno
import org.ncapas.canchitas.repositories.TipoCanchaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TipoCanchaSeeder implements CommandLineRunner {

    private final TipoCanchaRepository tipoRepo;

    @Override
    public void run(String... args) {
        if (tipoRepo.count() == 0) {
            tipoRepo.save(TipoCancha.builder().tipo(Tipo.FUTBOLL_RAPIDO).build());
            tipoRepo.save(TipoCancha.builder().tipo(Tipo.GRAMA_ARTIFICIAL).build());
            System.out.println("Tipos de cancha precargados ✓");
        }
    }
}
