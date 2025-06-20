package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombreIgnoreCase(String nombre);

}
