package org.melosas.pasteleria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.melosas.pasteleria.enums.EstadoCompra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponseDTO {
    private Long id;
    private String proveedorNombre;
    private String proveedorContacto;
    private LocalDateTime fechaCompra;
    private BigDecimal costoTransporte;
    private BigDecimal totalProductos;
    private BigDecimal totalCompra;
    private BigDecimal gananciaObtenida;
    private EstadoCompra estadoCompra;
    private String notas;
    private List<DetalleCompraDTO> items;
}
