package org.melosas.pasteleria.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-pago", path = "/api/v1/pagos")
public interface PagoClient {

    @GetMapping("/venta/{ventaId}")
    List<Object> obtenerPagosPorVenta(@PathVariable("ventaId") Long ventaId);
}
