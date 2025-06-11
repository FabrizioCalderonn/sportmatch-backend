package org.ncapas.canchitas.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "metodo_pago")
public class MetodoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;

    @Column(name = "metodo_pago", nullable = false)
    private Metodo metodoPago;

    public enum Metodo {
        TARJETA_DEBITO,
        TARJETA_CREDITO;

        @JsonCreator
        public static Metodo from(String value) {
            return Metodo.valueOf(value.trim().toUpperCase());
        }

        @JsonValue
        public String toValue() {
            return this.name();
        }
    }
}
