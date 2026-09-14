package org.melosas.pasteleria.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.melosas.pasteleria.dto.AjusteStockDTO;
import org.melosas.pasteleria.dto.InventarioItemRequestDTO;
import org.melosas.pasteleria.dto.InventarioItemResponseDTO;
import org.melosas.pasteleria.model.MovimientoInventario;
import org.melosas.pasteleria.service.InventarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public List<InventarioItemResponseDTO> listarTodos() {
        return inventarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public InventarioItemResponseDTO obtenerPorId(@PathVariable Long id) {
        return inventarioService.obtenerPorId(id);
    }

    @GetMapping("/codigo/{codigoPastel}")
    public InventarioItemResponseDTO obtenerPorCodigo(@PathVariable String codigoPastel) {
        return inventarioService.obtenerPorCodigo(codigoPastel);
    }

    @GetMapping("/codigo/{codigoPastel}/stock")
    public Integer obtenerStockPorCodigo(@PathVariable String codigoPastel) {
        return inventarioService.obtenerStockPorCodigo(codigoPastel);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventarioItemResponseDTO crear(@Valid @RequestBody InventarioItemRequestDTO dto) {
        return inventarioService.crear(dto);
    }

    @PutMapping("/{id}")
    public InventarioItemResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody InventarioItemRequestDTO dto) {
        return inventarioService.actualizar(id, dto);
    }

    @PutMapping("/codigo/{codigoPastel}/descontar")
    public InventarioItemResponseDTO descontarStock(@PathVariable String codigoPastel, @RequestParam Integer cantidad) {
        return inventarioService.descontarStock(codigoPastel, cantidad);
    }

    @PutMapping("/codigo/{codigoPastel}/descontar-costo")
    public InventarioItemResponseDTO descontarStockCosto(@PathVariable String codigoPastel, @RequestParam Integer cantidad) {
        return inventarioService.descontarStockCosto(codigoPastel, cantidad);
    }

    @PatchMapping("/{id}/ajuste")
    public InventarioItemResponseDTO ajustarStock(@PathVariable Long id, @Valid @RequestBody AjusteStockDTO dto) {
        return inventarioService.ajustarStock(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
    }
}
