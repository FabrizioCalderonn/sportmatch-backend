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
@Table(name = "canchas")
public class Cancha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cancha", nullable = false)
    private Integer idCancha;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "foto")
    private String foto;

    @Column(name = "numero_cancha", nullable = false)
    private Integer numeroCancha;

    @ManyToOne
    @JoinColumn(name = "id_tipo_cancha", nullable = false, foreignKey = @ForeignKey(name = "fk_tipo_cancha"))
    private TipoCancha tipoCancha;

    @ManyToOne
    @JoinColumn(name = "id_jornada", nullable = false, foreignKey = @ForeignKey(name = "fk_jornada"))
    private Jornada jornada;

    @ManyToOne
    @JoinColumn(name = "id_lugar", nullable = false, foreignKey = @ForeignKey(name = "fk_lugar"))
    private Lugar lugar;
}
