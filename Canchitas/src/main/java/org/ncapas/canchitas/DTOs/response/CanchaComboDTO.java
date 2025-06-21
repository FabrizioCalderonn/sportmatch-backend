// DTOs/response/CanchaComboDTO.java
package org.ncapas.canchitas.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class CanchaComboDTO {
    private Integer id;
    private String  nombre;
    private List<String> imagenes;
    private String tipoCancha;   // “FUTBOLL_RAPIDO”, “GRAMA_ARTIFICIAL”, …
}
