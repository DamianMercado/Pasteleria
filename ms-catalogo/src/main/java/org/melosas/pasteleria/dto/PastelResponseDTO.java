package org.melosas.pasteleria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PastelResponseDTO {
    private Long id;
    private String codigoPastel;
    private String nombrePastel;
    private String categoria;
    private BigDecimal precioPastel;
    private BigDecimal precioVenta;
    private BigDecimal precioCosto;
    private Integer pesoPastel;
    private LocalDate fechaVencimiento;
    private Boolean vencido;
    private Long diasParaVencer;
    private Long compraId;
    
    private Integer stock;
    private String estadoStock;
}
