package org.melosas.pasteleria.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-venta")
public interface VentaClient {
    @GetMapping("/api/v1/ventas/{id}")
    Object obtenerVenta(@PathVariable("id") Long id);
}
