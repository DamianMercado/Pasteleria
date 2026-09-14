package org.melosas.pasteleria.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.melosas.pasteleria.dto.ActualizarEstadoCompraDTO;
import org.melosas.pasteleria.dto.CompraRequestDTO;
import org.melosas.pasteleria.dto.CompraResponseDTO;
import org.melosas.pasteleria.service.CompraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @GetMapping
    public ResponseEntity<List<CompraResponseDTO>> listarTodas() {
        return ResponseEntity.ok(compraService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CompraResponseDTO> crearOrdenCompra(@Valid @RequestBody CompraRequestDTO dto) {
        return new ResponseEntity<>(compraService.crearOrdenCompra(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CompraResponseDTO> actualizarEstado(@PathVariable Long id, @Valid @RequestBody ActualizarEstadoCompraDTO dto) {
        return ResponseEntity.ok(compraService.actualizarEstado(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarCompra(@PathVariable Long id) {
        compraService.cancelarCompra(id);
        return ResponseEntity.noContent().build();
    }
}
