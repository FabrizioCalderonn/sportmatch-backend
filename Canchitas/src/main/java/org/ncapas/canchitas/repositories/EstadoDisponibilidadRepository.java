package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.EstadoDisponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoDisponibilidadRepository extends JpaRepository<EstadoDisponibilidad, Long> {
}
