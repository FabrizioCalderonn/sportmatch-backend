package org.ncapas.canchitas.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
@Table(name = "tipo_cancha")
public class TipoCancha {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_tipo_cancha")
private Integer idTipoCancha;

@Column(name = "tipo", nullable = false, unique = true)
private Tipo tipo;

public enum Tipo {
    FUTBOLL_RAPIDO("Fútbol Rápido"),
    GRAMA_ARTIFICIAL("Grama Artificial");

    private final String displayName;  

    Tipo(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static Tipo from(String value) {
        return Tipo.valueOf(value.trim().toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
}
