package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JornadaRepository extends JpaRepository<Jornada, Integer> {


}
