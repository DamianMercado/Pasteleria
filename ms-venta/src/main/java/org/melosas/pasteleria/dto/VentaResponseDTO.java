package org.melosas.pasteleria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.melosas.pasteleria.enums.EstadoVenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponseDTO {
    private Long id;
    private LocalDateTime fechaVenta;
    private String calle;
    private String ciudad;
    private String clienteNombre;
    private BigDecimal totalVenta;
    private EstadoVenta estadoVenta;
    private String notas;
    private List<DetalleVentaDTO> items;
}
