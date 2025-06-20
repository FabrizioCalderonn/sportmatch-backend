package org.ncapas.canchitas.Controllers;



import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.DTOs.request.AuthRequestDTO;
import org.ncapas.canchitas.DTOs.response.AuthResponseDTO;
import org.ncapas.canchitas.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
        import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getCorreo(), req.getContrasena()));

        String token = jwtUtil.generate(req.getCorreo());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
