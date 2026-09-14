package org.melosas.pasteleria.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-inventario")
public interface InventarioClient {
    @GetMapping("/api/v1/inventario/codigo/{codigoPastel}/stock")
    Integer obtenerStock(@PathVariable String codigoPastel);
}
