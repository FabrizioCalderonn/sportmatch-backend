package org.ncapas.canchitas.repositories;

import org.ncapas.canchitas.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepostitory extends JpaRepository<Usuario, Long> {
    //Para buscar en login y registro
    Optional<Usuario> findByNombre(String correo);
}
