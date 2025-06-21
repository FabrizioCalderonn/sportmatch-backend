package org.ncapas.canchitas.security.config;

import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.security.JwtFilter;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity        //  ← añade esta línea
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // autenticación y login siguen públicos

                        /* ---------- Rutas públicas ---------- */
                        .requestMatchers("/api/auth/**").permitAll()                      // login
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()    // registro
                        .requestMatchers(HttpMethod.GET,  "/api/lugares/zonas").permitAll() // combo de zonas ← NUEVO
                        .requestMatchers(HttpMethod.GET, "/api/canchas/tipos").permitAll() // Obtemer canchas
                        .requestMatchers(HttpMethod.GET, "/api/canchas/{id}").permitAll() // Obtemer canchas


                        /* ---------- ADMINISTRADOR Y USUARIO---------- */
                        // 1) zonas  →  lugares
                        .requestMatchers(HttpMethod.GET,
                                "/api/zonas/*/lugares"        // p.e. /api/zonas/3/lugares
                        ).hasAnyRole("ADMIN", "CLIENTE")

                        // 2) lugares →  canchas
                        .requestMatchers(HttpMethod.GET,
                                "/api/lugares/*/canchas"      // p.e. /api/lugares/7/canchas
                        ).hasAnyRole("ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/reservas").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/usuario/{id}").hasAnyRole("ADMIN","CLIENTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservas/{id}").hasAnyRole("ADMIN","CLIENTE")

                        // *** rol admin ***
                        .requestMatchers(HttpMethod.POST,   "/api/lugares").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/lugares/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/canchas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/canchas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservas").hasRole("ADMIN")
                        // todo lo demás necesita token
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}