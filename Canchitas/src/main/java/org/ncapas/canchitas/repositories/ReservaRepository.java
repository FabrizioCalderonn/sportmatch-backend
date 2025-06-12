package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    //para buscar mis reservaciones por id del usuario
    List<Reserva> findByUsuarioId(Integer idUsuario);
    //para buscar las reservas por id de la cancha
    List<Reserva> findByCanchaId(Integer idCancha);
    //para ver las reservas por id del lugar de la cancha
    List<Reserva> findByLugarCanchaIdLugar(Integer idLugar);
    //para buscar las reservas por id del usuario y id de la cancha
    List<Reserva> findByUsuarioIdAndCanchaId(Integer idUsuario, Integer idCancha);
    //para mostrar las reservas por su estado
    List<Reserva> findByEstadoReserva(Reserva.EstadoReserva estadoReserva);
    //para mostrar las reservas por id del usuario y su estado
    List<Reserva> findByUsuarioIdAndEstadoReserva(Integer idUsuario, Reserva.EstadoReserva estadoReserva);
    //para mostrar las reservas por id del lugar de la cancha y su estado
    List<Reserva> findByCanchaLugarCanchaIdLugarAndEstadoReserva(Integer idLugar, Reserva.EstadoReserva estadoReserva);
}
