package org.melosas.pasteleria.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "ms-pago", path = "/api/v1/pagos")
public interface PagoClient {

    @GetMapping("/venta/{ventaId}")
    List<Object> obtenerPagosPorVenta(@PathVariable("ventaId") Long ventaId);

    @PostMapping
    Object procesarPago(@RequestBody PagoRequestDTO dto);

    record PagoRequestDTO(
        Long ventaId,
        String clienteNombre,
        BigDecimal monto,
        String metodoPago,
        LocalDateTime fechaVencimiento,
        String notas
    ) {}
}
