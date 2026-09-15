package org.melosas.pasteleria.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-inventario")
public interface InventarioClient {
    @GetMapping("/api/v1/inventario/codigo/{codigoPastel}/stock")
    Integer obtenerStock(@PathVariable("codigoPastel") String codigoPastel);
    
    @PutMapping("/api/v1/inventario/codigo/{codigoPastel}/descontar")
    void descontarStock(@PathVariable("codigoPastel") String codigoPastel, @RequestParam("cantidad") Integer cantidad);
    
    @PutMapping("/api/v1/inventario/codigo/{codigoPastel}/descontar-costo")
    void descontarStockCosto(@PathVariable("codigoPastel") String codigoPastel, @RequestParam("cantidad") Integer cantidad);

    @GetMapping("/api/v1/inventario/codigo/{codigoPastel}")
    InventarioItemDTO obtenerPorCodigo(@PathVariable("codigoPastel") String codigoPastel);

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    record InventarioItemDTO(Long id, String codigoPastel, String nombrePastel, Integer stock, Integer ventaCosto, Long compraId, Boolean vencido) {}
}
