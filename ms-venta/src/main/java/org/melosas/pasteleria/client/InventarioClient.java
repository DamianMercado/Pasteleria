package org.melosas.pasteleria.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-inventario")
public interface InventarioClient {
    @GetMapping("/api/v1/inventario/codigo/{codigoPastel}/stock")
    Integer obtenerStock(@PathVariable String codigoPastel);
    
    @PutMapping("/api/v1/inventario/codigo/{codigoPastel}/descontar")
    void descontarStock(@PathVariable String codigoPastel, @RequestParam Integer cantidad);
    
    @PutMapping("/api/v1/inventario/codigo/{codigoPastel}/descontar-costo")
    void descontarStockCosto(@PathVariable String codigoPastel, @RequestParam Integer cantidad);
}
