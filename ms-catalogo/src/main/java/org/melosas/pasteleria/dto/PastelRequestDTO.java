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
public class PastelRequestDTO {
    @NotBlank
    private String codigoPastel;

    @NotBlank
    private String nombrePastel;

    private String categoria;

    @NotNull
    @Positive
    private BigDecimal precioPastel;

    @NotNull
    @Positive
    private BigDecimal precioVenta;

    @Positive
    private Integer pesoPastel;

    @Positive
    private Integer diasVencimiento;

    private Long compraId;
}
