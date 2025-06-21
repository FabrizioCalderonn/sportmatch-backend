package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    // Busca todas las reservas por id del usuario
    List<Reserva> findByUsuario_IdUsuario(Integer usuarioId);
    // Busca todas las reservas por id de la cancha
    List<Reserva> findByCancha_IdCancha(Integer idCancha);

    // Busca todas las reservas cuyo lugar (en cancha.lugar) tenga este id
    List<Reserva> findByCancha_Lugar_IdLugar(Integer idLugar);

    // Busca reservas por usuario y cancha
    List<Reserva> findByUsuario_IdUsuarioAndCancha_IdCancha(Integer idUsuario, Integer idCancha);

    // Busca reservas por estado
    List<Reserva> findByEstadoReserva(Reserva.EstadoReserva estadoReserva);

    // Busca reservas por usuario y estado
    List<Reserva> findByUsuario_IdUsuarioAndEstadoReserva(Integer idUsuario, Reserva.EstadoReserva estadoReserva);

    // Busca reservas por lugar y estado
    List<Reserva> findByCancha_Lugar_IdLugarAndEstadoReserva(Integer idLugar, Reserva.EstadoReserva estadoReserva);
    List<Reserva> findByFechaReserva(java.util.Date fechaReserva);

}


