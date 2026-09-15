package org.melosas.pasteleria.client;

import org.melosas.pasteleria.dto.client.CompraDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ms-compra")
public interface CompraClient {

    @GetMapping("/api/v1/compras")
    List<CompraDTO> listarCompras();

    @GetMapping("/api/v1/compras/{id}")
    CompraDTO obtenerPorId(@PathVariable("id") Long id);
}