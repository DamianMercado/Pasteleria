package org.melosas.pasteleria.controller.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.melosas.pasteleria.client.CompraClient;
import org.melosas.pasteleria.dto.InventarioItemRequestDTO;
import org.melosas.pasteleria.dto.InventarioItemResponseDTO;
import org.melosas.pasteleria.dto.client.CompraDTO;
import org.melosas.pasteleria.service.InventarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/web/inventario")
@RequiredArgsConstructor
public class InventarioWebController {

    private final InventarioService inventarioService;
    private final CompraClient compraClient;

    private List<CompraDTO> obtenerComprasSeguras() {
        try {
            List<CompraDTO> compras = compraClient.listarCompras();
            return compras != null ? compras : Collections.emptyList();
        } catch (Exception e) {
            log.warn("No se pudieron cargar las compras desde ms-compra: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @GetMapping
    public String listarInventario(Model model) {
        try {
            model.addAttribute("items", inventarioService.listarTodos());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "inventario/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("item", new InventarioItemRequestDTO());
        model.addAttribute("compras", obtenerComprasSeguras());
        return "inventario/formulario";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            InventarioItemResponseDTO response = inventarioService.obtenerPorId(id);
            InventarioItemRequestDTO requestDTO = new InventarioItemRequestDTO();
            requestDTO.setCodigoPastel(response.getCodigoPastel());
            requestDTO.setNombrePastel(response.getNombrePastel());
            requestDTO.setStock(response.getStock());
            requestDTO.setCompraId(response.getCompraId());
            requestDTO.setFechaVencimiento(response.getFechaVencimiento());
            model.addAttribute("item", requestDTO);
            model.addAttribute("compras", obtenerComprasSeguras());
            model.addAttribute("id", response.getId());
            return "inventario/formulario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/inventario";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("item") InventarioItemRequestDTO dto,
                          BindingResult result,
                          @RequestParam(required = false) Long id,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("compras", obtenerComprasSeguras());
            if (id != null) model.addAttribute("id", id);
            return "inventario/formulario";
        }

        try {
            if (id == null) {
                inventarioService.crear(dto);
                redirectAttributes.addFlashAttribute("mensaje", "Ítem creado con éxito");
            } else {
                inventarioService.actualizar(id, dto);
                redirectAttributes.addFlashAttribute("mensaje", "Ítem actualizado con éxito");
            }
            return "redirect:/web/inventario";
        } catch (Exception e) {
            model.addAttribute("compras", obtenerComprasSeguras());
            model.addAttribute("error", e.getMessage());
            if (id != null) model.addAttribute("id", id);
            return "inventario/formulario";
        }
    }

    @GetMapping("/api/compras")
    @ResponseBody
    public List<CompraDTO> obtenerComprasApi() {
        return obtenerComprasSeguras();
    }

    @GetMapping("/api/compras/{id}")
    @ResponseBody
    public ResponseEntity<CompraDTO> obtenerCompraApi(@PathVariable Long id) {
        try {
            CompraDTO compra = compraClient.obtenerPorId(id);
            return ResponseEntity.ok(compra);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inventarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Ítem eliminado con éxito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/inventario";
    }

    @GetMapping("/{codigoPastel}/movimientos")
    public String verMovimientos(@PathVariable String codigoPastel, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("codigoPastel", codigoPastel);
            model.addAttribute("movimientos", inventarioService.obtenerMovimientos(codigoPastel));
            return "inventario/movimientos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/inventario";
        }
    }
}
