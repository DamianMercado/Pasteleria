package org.melosas.pasteleria.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "ms-catalogo")
public interface CatalogoClient {
    @GetMapping("/api/v1/catalogo/codigo/{codigoPastel}")
    PastelDTO obtenerPorCodigo(@PathVariable String codigoPastel);
    
    record PastelDTO(Long id, String codigoPastel, String nombrePastel, BigDecimal precioPastel, BigDecimal precioVenta, BigDecimal precioCosto) {}
}
