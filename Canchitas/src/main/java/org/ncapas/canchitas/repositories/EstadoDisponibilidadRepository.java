package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.EstadoDisponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoDisponibilidadRepository extends JpaRepository<EstadoDisponibilidad, Integer> {
    Optional<EstadoDisponibilidad> findByEstado(EstadoDisponibilidad.Status estado);
}
