package org.ncapas.canchitas;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.entities.Rol;
import org.ncapas.canchitas.repositories.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataseederRol implements CommandLineRunner {

    private final RolRepository rolRepo;

    @Override
    public void run(String... args) {
        if (rolRepo.count() == 0) {
            rolRepo.save(Rol.builder().nombre("ADMIN").build());
            rolRepo.save(Rol.builder().nombre("CLIENTE").build());
            System.out.println("Roles precargados!");
        }
    }
}