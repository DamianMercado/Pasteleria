package org.melosas.pasteleria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class InventarioItemRequestDTO {
    @NotBlank(message = "El código del pastel es requerido")
    private String codigoPastel;

    @NotBlank(message = "El nombre del pastel es requerido")
    private String nombrePastel;

    @NotNull(message = "El stock es requerido")
    @PositiveOrZero(message = "El stock debe ser 0 o positivo")
    private Integer stock;

    private Long compraId;
}
