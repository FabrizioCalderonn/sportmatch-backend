package org.ncapas.canchitas.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    @Column(name = "fecha_reserva", nullable = false)
    private Date fechaReserva;

    @Column(name = "hora_entrada", nullable = false)
    private LocalTime horaEntrada;

    @Column(name = "hora_salida", nullable = false)
    private LocalTime horaSalida;

    @Column(name = "precio_total", nullable = false)
    private Double precioTotal;

    @Column(name = "fecha_creacion", nullable = false)
    private Date fechaCreacion;

    @Column(name = "estado_reserva", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoReserva estadoReserva;

    public enum EstadoReserva {
        PENDIENTE,
        FINALIZADA;

        @JsonCreator
        public static EstadoReserva from(String value) {
            return EstadoReserva.valueOf(value.trim().toUpperCase(Locale.ROOT));
        }

        @JsonValue
        public String toValue() {
            return this.name();
        }
    }

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false, foreignKey = @ForeignKey(name = "fk_usuario"))
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_lugar", nullable = false, foreignKey = @ForeignKey(name = "fk_lugar"))
    private Lugar lugar;

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago", nullable = false, foreignKey = @ForeignKey(name = "fk_metodo_pago"))
    private MetodoPago metodoPago;

    @ManyToOne
    @JoinColumn(name = "id_cancha", nullable = false, foreignKey = @ForeignKey(name = "fk_cancha"))
    private Cancha cancha;
}
