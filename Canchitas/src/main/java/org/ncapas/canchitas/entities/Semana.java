package org.ncapas.canchitas.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

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

    @Column(name = "dia")
    private Dia dia;

    public enum Dia {
        LUNES,
        MARTES,
        MIERCOLES,
        JUEVES,
        VIERNES,
        SABADO,
        DOMINGO;

        public static Dia from(String value) {
            return Dia.valueOf(value.trim().toUpperCase());
        }

        @Override
        public String toString() {
            return this.name();
        }
    }
}
