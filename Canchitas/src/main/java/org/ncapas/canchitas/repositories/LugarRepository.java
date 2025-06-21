package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.Lugar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LugarRepository extends JpaRepository <Lugar, Integer> {
    List<Lugar> findByZona_IdZona(Integer idZona);
}
