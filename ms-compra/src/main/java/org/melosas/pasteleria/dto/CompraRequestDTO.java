package org.melosas.pasteleria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRequestDTO {
    @NotBlank
    private String proveedorNombre;

    private String proveedorContacto;

    @NotNull
    @PositiveOrZero
    private BigDecimal costoTransporte;

    private String notas;

    @NotNull
    private List<DetalleCompraDTO> items;
}
