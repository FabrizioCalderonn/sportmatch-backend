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
@Table(name = "estado_disponibilidad")
public class EstadoDisponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_disponibilidad")
    private Integer idEstadoDisponibilidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private Status estado;

   public enum Status{
        DISPONIBLE,
        NO_DISPONIBLE;

        public static Status from(String value) {
            return Status.valueOf(value.trim().toUpperCase());
        }

        @Override
        public String toString() {
            return this.name();
        }
   }
}
