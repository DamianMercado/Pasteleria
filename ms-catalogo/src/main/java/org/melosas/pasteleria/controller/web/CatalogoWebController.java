package org.melosas.pasteleria.controller.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.melosas.pasteleria.client.CompraClient;
import org.melosas.pasteleria.dto.PastelRequestDTO;
import org.melosas.pasteleria.dto.PastelResponseDTO;
import org.melosas.pasteleria.dto.client.CompraDTO;
import org.melosas.pasteleria.service.PastelService;
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
@RequestMapping("/web/catalogo")
@RequiredArgsConstructor
public class CatalogoWebController {

    private final PastelService pastelService;
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
    public String listarTodos(Model model) {
        model.addAttribute("pasteles", pastelService.listarTodos());
        return "catalogo/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("pastel", new PastelRequestDTO());
        model.addAttribute("compras", obtenerComprasSeguras());
        model.addAttribute("titulo", "Nuevo Pastel");
        return "catalogo/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            PastelResponseDTO pastel = pastelService.obtenerPorId(id);
            PastelRequestDTO dto = PastelRequestDTO.builder()
                    .codigoPastel(pastel.getCodigoPastel())
                    .nombrePastel(pastel.getNombrePastel())
                    .categoria(pastel.getCategoria())
                    .precioPastel(pastel.getPrecioPastel())
                    .precioVenta(pastel.getPrecioVenta())
                    .pesoPastel(pastel.getPesoPastel())
                    .fechaVencimiento(pastel.getFechaVencimiento())
                    .compraId(pastel.getCompraId())
                    .build();
            
            model.addAttribute("pastel", dto);
            model.addAttribute("compras", obtenerComprasSeguras());
            model.addAttribute("id", id);
            model.addAttribute("titulo", "Editar Pastel");
            return "catalogo/formulario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/catalogo";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("pastel") PastelRequestDTO dto, 
                          BindingResult result,
                          @RequestParam(required = false) Long id, 
                          Model model, 
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("compras", obtenerComprasSeguras());
            model.addAttribute("titulo", id == null ? "Nuevo Pastel" : "Editar Pastel");
            if (id != null) {
                model.addAttribute("id", id);
            }
            return "catalogo/formulario";
        }
        
        try {
            if (id == null) {
                pastelService.crear(dto);
                redirectAttributes.addFlashAttribute("mensaje", "Pastel creado exitosamente.");
            } else {
                pastelService.actualizar(id, dto);
                redirectAttributes.addFlashAttribute("mensaje", "Pastel actualizado exitosamente.");
            }
        } catch (Exception e) {
            model.addAttribute("compras", obtenerComprasSeguras());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("titulo", id == null ? "Nuevo Pastel" : "Editar Pastel");
            if (id != null) {
                model.addAttribute("id", id);
            }
            return "catalogo/formulario";
        }
        
        return "redirect:/web/catalogo";
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
            pastelService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Pastel eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/web/catalogo";
    }
}
