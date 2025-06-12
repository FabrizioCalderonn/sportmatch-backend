package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZonaRepository extends JpaRepository<Zona, Long> {
}
