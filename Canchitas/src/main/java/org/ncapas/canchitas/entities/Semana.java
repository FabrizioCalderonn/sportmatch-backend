package org.ncapas.canchitas.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "semana")
public class Semana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_semana")
    private Integer idSemana;

    @Enumerated(EnumType.STRING)           // ← guarda LUNES, MARTES, …
    @Column(name = "dia", nullable = false)
    private Dia dia;

    public enum Dia {
        LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO;

        public static Dia from(String value) {
            return Dia.valueOf(value.trim().toUpperCase());
        }
    }
}
