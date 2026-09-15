package org.melosas.pasteleria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCompraDTO {
    @NotBlank
    private String nombreProducto;

    @NotNull
    @Positive
    private Integer cantidad;

    @NotNull
    @Positive
    private BigDecimal precioUnitario;

    public String getCodigoPastel() {
        return nombreProducto;
    }

    public void setCodigoPastel(String codigoPastel) {
        this.nombreProducto = codigoPastel;
    }
}
