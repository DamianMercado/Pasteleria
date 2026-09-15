package org.melosas.pasteleria.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "ms-venta")
public interface VentaClient {
    @GetMapping("/api/v1/ventas/{id}")
    VentaDTO obtenerVenta(@PathVariable("id") Long id);

    @JsonIgnoreProperties(ignoreUnknown = true)
    record VentaDTO(
        Long id,
        LocalDateTime fechaVenta,
        BigDecimal totalVenta,
        String clienteNombre,
        String calle,
        String ciudad,
        String estadoVenta,
        String notas,
        List<DetalleVentaItemDTO> items
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record DetalleVentaItemDTO(
        Long id,
        String codigoPastel,
        String nombrePastel,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        Boolean vendidoACosto
    ) {}
}
