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
@Table(name = "lugares")
public class Lugar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lugar")
    private Integer idLugar;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    @Column(name = "codigo_establecimiento", nullable = false)
    private Integer capacidad;

    @ManyToOne
    @JoinColumn(name = "id_zona", nullable = false, foreignKey = @ForeignKey(name = "fk_zona"))
    private Zona zona;

}
