package org.melosas.pasteleria.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.melosas.pasteleria.enums.EstadoCompra;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoCompraDTO {
    @NotNull
    private EstadoCompra nuevoEstado;
}
