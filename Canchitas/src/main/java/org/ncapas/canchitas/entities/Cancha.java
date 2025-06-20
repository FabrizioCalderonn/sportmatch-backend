package org.ncapas.canchitas.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "cancha_fotos",
            joinColumns = @JoinColumn(name = "id_cancha"))
    @Column(name = "url_foto", nullable = false)
    private List<String> imagenes = new ArrayList<>();

    @Column(name = "numero_cancha", nullable = false)
    private Integer numeroCancha;

    @ManyToOne
    @JoinColumn(name = "id_tipo_cancha", nullable = false, foreignKey = @ForeignKey(name = "fk_tipo_cancha"))
    private TipoCancha tipoCancha;


    @OneToMany(mappedBy = "cancha",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Jornada> jornadas = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_lugar", nullable = false, foreignKey = @ForeignKey(name = "fk_lugar"))
    private Lugar lugar;
}
