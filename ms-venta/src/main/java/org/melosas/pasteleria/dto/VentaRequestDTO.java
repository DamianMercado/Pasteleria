package org.melosas.pasteleria.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequestDTO {
    private String calle;
    private String ciudad;
    private String clienteNombre;
    private String notas;

    @NotNull
    @NotEmpty
    @Valid
    private List<DetalleVentaDTO> items;
}
