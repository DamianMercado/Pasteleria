package org.melosas.pasteleria.dto;

import lombok.Data;

@Data
public class InventarioItemResponseDTO {
    private Long id;
    private String codigoPastel;
    private String nombrePastel;
    private Integer stock;
    private Integer ventaCosto;
    private Long compraId;
    private Boolean alertaStockBajo;
}
