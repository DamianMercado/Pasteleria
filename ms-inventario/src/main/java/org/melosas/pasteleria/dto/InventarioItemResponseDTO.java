package org.melosas.pasteleria.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class InventarioItemResponseDTO {
    private Long id;
    private String codigoPastel;
    private String nombrePastel;
    private Integer stock;
    private Integer ventaCosto;
    private Long compraId;
    private Boolean alertaStockBajo;
    private LocalDate fechaVencimiento;
    private Boolean vencido;
    private Long diasParaVencer;
    private Integer stockTotalProducto;
}
