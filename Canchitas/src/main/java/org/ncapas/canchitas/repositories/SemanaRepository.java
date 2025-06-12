package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.Semana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemanaRepository extends JpaRepository<Semana, Long> {
}
