package org.melosas.pasteleria.controller.web;

import lombok.RequiredArgsConstructor;
import org.melosas.pasteleria.dto.ActualizarEstadoCompraDTO;
import org.melosas.pasteleria.dto.CompraRequestDTO;
import org.melosas.pasteleria.dto.DetalleCompraDTO;
import org.melosas.pasteleria.enums.EstadoCompra;
import org.melosas.pasteleria.service.CompraService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/web/compras")
@RequiredArgsConstructor
public class CompraWebController {

    private final CompraService compraService;

    @GetMapping
    public String listarCompras(Model model) {
        try {
            model.addAttribute("compras", compraService.listarTodas());
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las compras: " + e.getMessage());
        }
        return "compras/lista";
    }

    @GetMapping("/nueva")
    public String nuevaCompra(Model model) {
        return "compras/formulario";
    }

    @PostMapping("/guardar")
    public String guardarCompra(
            @RequestParam String proveedorNombre,
            @RequestParam(required = false) String proveedorContacto,
            @RequestParam BigDecimal costoTransporte,
            @RequestParam(required = false) String notas,
            @RequestParam(name = "nombreProducto") String[] nombresProducto,
            @RequestParam(name = "cantidad") Integer[] cantidades,
            @RequestParam(name = "precioUnitario") BigDecimal[] preciosUnitarios,
            RedirectAttributes redirectAttributes) {

        try {
            List<DetalleCompraDTO> items = new ArrayList<>();
            if (nombresProducto != null) {
                for (int i = 0; i < nombresProducto.length; i++) {
                    if (nombresProducto[i] != null && !nombresProducto[i].trim().isEmpty()) {
                        DetalleCompraDTO detalle = DetalleCompraDTO.builder()
                                .nombreProducto(nombresProducto[i])
                                .cantidad(cantidades[i])
                                .precioUnitario(preciosUnitarios[i])
                                .build();
                        items.add(detalle);
                    }
                }
            }

            CompraRequestDTO dto = CompraRequestDTO.builder()
                    .proveedorNombre(proveedorNombre)
                    .proveedorContacto(proveedorContacto)
                    .costoTransporte(costoTransporte)
                    .notas(notas)
                    .items(items)
                    .build();

            compraService.crearOrdenCompra(dto);
            redirectAttributes.addFlashAttribute("mensaje", "Compra registrada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar la compra: " + e.getMessage());
            return "redirect:/web/compras/nueva";
        }
        return "redirect:/web/compras";
    }

    @GetMapping("/{id}")
    public String detalleCompra(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("compra", compraService.obtenerPorId(id));
            return "compras/detalle";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/compras";
        }
    }

    @GetMapping("/{id}/estado")
    public String mostrarFormularioEstado(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("compra", compraService.obtenerPorId(id));
            model.addAttribute("estados", EstadoCompra.values());
            return "compras/estado";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/compras";
        }
    }

    @PostMapping("/{id}/estado")
    public String actualizarEstado(@PathVariable Long id, @RequestParam EstadoCompra nuevoEstado, RedirectAttributes redirectAttributes) {
        try {
            ActualizarEstadoCompraDTO dto = new ActualizarEstadoCompraDTO(nuevoEstado);
            compraService.actualizarEstado(id, dto);
            redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar estado: " + e.getMessage());
        }
        return "redirect:/web/compras";
    }

    @GetMapping("/{id}/cancelar")
    public String cancelarCompra(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            compraService.cancelarCompra(id);
            redirectAttributes.addFlashAttribute("mensaje", "Compra cancelada exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar la compra: " + e.getMessage());
        }
        return "redirect:/web/compras";
    }
}
