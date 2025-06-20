// DTOs/response/TipoCanchaComboDTO.java
package org.ncapas.canchitas.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TipoCanchaComboDTO {
    private Integer id;
    private String  nombre;   // FUTBOLL_RAPIDO, GRAMA_ARTIFICIAL, ...
}
