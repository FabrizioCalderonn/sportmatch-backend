package org.ncapas.canchitas.DTOs.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservaResponseDTO {
    private Integer idReserva;
    private String nombreUsuario;
    private String nombreCancha;
    private String nombreLugar;
    private String metodoPago;
    private String estadoReserva;
    private String fechaReserva;
    private String fechaCreacion;
    private String horaEntrada;
    private String horaSalida;
    private Double precioTotal;
}
