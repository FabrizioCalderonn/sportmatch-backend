package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.TipoCancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoCanchaRepository extends JpaRepository<TipoCancha, Long> {
}
