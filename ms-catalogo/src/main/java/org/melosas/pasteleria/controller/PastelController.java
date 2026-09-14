package org.melosas.pasteleria.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.melosas.pasteleria.dto.PastelRequestDTO;
import org.melosas.pasteleria.dto.PastelResponseDTO;
import org.melosas.pasteleria.service.PastelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogo")
@RequiredArgsConstructor
public class PastelController {

    private final PastelService pastelService;

    @GetMapping
    public ResponseEntity<List<PastelResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pastelService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PastelResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pastelService.obtenerPorId(id));
    }

    @GetMapping("/codigo/{codigoPastel}")
    public ResponseEntity<PastelResponseDTO> obtenerPorCodigo(@PathVariable String codigoPastel) {
        return ResponseEntity.ok(pastelService.obtenerPorCodigo(codigoPastel));
    }

    @GetMapping("/compra/{compraId}")
    public ResponseEntity<List<PastelResponseDTO>> listarPorCompraId(@PathVariable Long compraId) {
        return ResponseEntity.ok(pastelService.listarPorCompraId(compraId));
    }

    @PostMapping
    public ResponseEntity<PastelResponseDTO> crear(@Valid @RequestBody PastelRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pastelService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PastelResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PastelRequestDTO dto) {
        return ResponseEntity.ok(pastelService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pastelService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
