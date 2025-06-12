package org.ncapas.canchitas.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "jornadas")
public class Jornada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jornada")
    private Integer idJornada;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "precio_por_hora", nullable = false)
    private Double precioPorHora;

    @ManyToOne
    @JoinColumn(name = "id_semana", nullable = false, foreignKey = @ForeignKey(name = "fk_semana"))
    private Semana semana;

    @ManyToOne
    @JoinColumn(name = "id_estado_disponibilidad", nullable = false, foreignKey = @ForeignKey(name = "fk_estado_disponibilidad"))
    private EstadoDisponibilidad estadoDisponibilidad;

}
