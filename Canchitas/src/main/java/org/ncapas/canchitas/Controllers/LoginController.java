package org.ncapas.canchitas.Controllers;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.DTOs.request.AuthRequestDTO;
import org.ncapas.canchitas.DTOs.response.AuthResponseDTO;
import org.ncapas.canchitas.DTOs.response.UsuarioResponseDTO;
import org.ncapas.canchitas.security.JwtUtil;
import org.ncapas.canchitas.Service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;   

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO req) {
        // 1) Autenticar
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getCorreo(), req.getContrasena()
                )
        );

        // 2) Generar token
        String token = jwtUtil.generate(req.getCorreo());

        // 3) Extraer el rol y quitar el prefijo "ROLE_"
        String authority = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("CLIENTE");  // por defecto

        String rol = authority.startsWith("ROLE_")
                ? authority.substring("ROLE_".length())
                : authority;

        // 4) Obtener datos del usuario (incluye idUsuario)
        UsuarioResponseDTO usuario = usuarioService.findByCorreo(req.getCorreo());

        // 5) Devolver token + rol + usuario
        return ResponseEntity.ok(new AuthResponseDTO(token, rol, usuario));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(
                Map.of("mensaje", "La sesión ha sido cerrada")
        );
    }
}
