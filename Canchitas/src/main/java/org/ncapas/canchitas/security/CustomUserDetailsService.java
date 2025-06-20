package org.ncapas.canchitas.security;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.entities.Usuario;
import org.ncapas.canchitas.repositories.UsuarioRepostitory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepostitory repo;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario u = repo.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Correo no registrado"));

        return new User(
                u.getCorreo(),
                u.getContrasena(),               // contraseña encriptada
                java.util.List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol().getNombre()))
        );
    }
}
