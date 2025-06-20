package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.Cancha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, Integer> {
    // Para buscar cancha por lugar
    List<Cancha> findByIdCancha(Integer idLugar);
    boolean existsByLugar_IdLugarAndNumeroCancha(Integer lugarId, Integer numeroCancha);

}
