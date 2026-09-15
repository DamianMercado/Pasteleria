package org.melosas.pasteleria.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompraDTO {
    private Long id;
    private String proveedorNombre;
    private String proveedorContacto;
    private LocalDateTime fechaCompra;
    private BigDecimal totalCompra;
    private List<DetalleCompraItemDTO> items;
}
