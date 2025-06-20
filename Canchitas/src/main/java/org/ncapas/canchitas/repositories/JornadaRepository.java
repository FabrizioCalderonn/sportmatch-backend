package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.Jornada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;

@Repository
public interface JornadaRepository extends JpaRepository<Jornada, Integer> {
    /** Consulta de solapamiento para un día concreto dentro de la misma cancha */
    boolean existsByCancha_IdCanchaAndSemana_IdSemanaAndHoraInicioLessThanAndHoraFinGreaterThan(
            Integer canchaId, Integer semanaId,
            LocalTime fin, LocalTime inicio);

}
