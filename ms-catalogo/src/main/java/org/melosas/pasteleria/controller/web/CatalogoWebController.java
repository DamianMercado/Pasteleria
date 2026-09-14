package org.melosas.pasteleria.controller.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.melosas.pasteleria.dto.PastelRequestDTO;
import org.melosas.pasteleria.dto.PastelResponseDTO;
import org.melosas.pasteleria.service.PastelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/catalogo")
@RequiredArgsConstructor
public class CatalogoWebController {

    private final PastelService pastelService;

    @GetMapping
    public String listarTodos(Model model) {
        model.addAttribute("pasteles", pastelService.listarTodos());
        return "catalogo/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("pastel", new PastelRequestDTO());
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
                    .diasVencimiento(pastel.getDiasVencimiento())
                    .compraId(pastel.getCompraId())
                    .build();
            
            model.addAttribute("pastel", dto);
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
            model.addAttribute("error", e.getMessage());
            model.addAttribute("titulo", id == null ? "Nuevo Pastel" : "Editar Pastel");
            if (id != null) {
                model.addAttribute("id", id);
            }
            return "catalogo/formulario";
        }
        
        return "redirect:/web/catalogo";
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
