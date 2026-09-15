package org.melosas.pasteleria.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetalleCompraItemDTO {
    private String nombreProducto;
    private String codigoPastel;
    private Integer cantidad;
    private BigDecimal precioUnitario;

    public String getCodigo() {
        if (codigoPastel != null && !codigoPastel.trim().isEmpty()) {
            return codigoPastel.trim();
        }
        return nombreProducto != null ? nombreProducto.trim() : "";
    }
}
