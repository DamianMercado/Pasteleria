package org.melosas.pasteleria.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.melosas.pasteleria.dto.PagoRequestDTO;
import org.melosas.pasteleria.dto.PagoResponseDTO;
import org.melosas.pasteleria.service.PagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<PagoResponseDTO>> obtenerPorVentaId(@PathVariable Long ventaId) {
        return ResponseEntity.ok(pagoService.obtenerPorVentaId(ventaId));
    }

    @GetMapping("/fiados")
    public ResponseEntity<List<PagoResponseDTO>> listarFiadosPendientes() {
        return ResponseEntity.ok(pagoService.listarFiadosPendientes());
    }

    @PostMapping
    public ResponseEntity<PagoResponseDTO> procesarPago(@Valid @RequestBody PagoRequestDTO dto) {
        return new ResponseEntity<>(pagoService.procesarPago(dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<PagoResponseDTO> marcarComoPagado(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.marcarComoPagado(id));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<Void> anularPago(@PathVariable Long id) {
        pagoService.anularPago(id);
        return ResponseEntity.noContent().build();
    }
}
