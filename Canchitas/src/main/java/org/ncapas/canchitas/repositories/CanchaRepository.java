package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, Long> {
    // Para buscar cancha por lugar
    List<Cancha> findByLugar(Integer idLugar);
}
