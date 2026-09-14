package org.melosas.pasteleria.controller.web;

import lombok.RequiredArgsConstructor;
import org.melosas.pasteleria.dto.DetalleVentaDTO;
import org.melosas.pasteleria.dto.VentaRequestDTO;
import org.melosas.pasteleria.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/web/ventas")
@RequiredArgsConstructor
public class VentaWebController {

    private final VentaService ventaService;

    @GetMapping
    public String listarVentas(Model model) {
        try {
            model.addAttribute("ventas", ventaService.listarTodas());
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las ventas: " + e.getMessage());
        }
        return "ventas/lista";
    }

    @GetMapping("/nueva")
    public String nuevaVentaForm(Model model) {
        return "ventas/formulario";
    }

    @PostMapping("/guardar")
    public String guardarVenta(
            @RequestParam String calle,
            @RequestParam String ciudad,
            @RequestParam(required = false) String clienteNombre,
            @RequestParam(required = false) String notas,
            @RequestParam(name = "codigoPastel", required = false) String[] codigos,
            @RequestParam(name = "nombrePastel", required = false) String[] nombres,
            @RequestParam(name = "cantidad", required = false) Integer[] cantidades,
            @RequestParam(name = "precioUnitario", required = false) BigDecimal[] precios,
            @RequestParam(name = "vendidoACosto", required = false) String[] costoStrings,
            RedirectAttributes redirectAttributes) {
        
        try {
            VentaRequestDTO request = new VentaRequestDTO();
            request.setCalle(calle);
            request.setCiudad(ciudad);
            request.setClienteNombre(clienteNombre);
            request.setNotas(notas);

            List<DetalleVentaDTO> items = new ArrayList<>();
            if (codigos != null) {
                for (int i = 0; i < codigos.length; i++) {
                    if (codigos[i] != null && !codigos[i].trim().isEmpty()) {
                        DetalleVentaDTO item = new DetalleVentaDTO();
                        item.setCodigoPastel(codigos[i]);
                        item.setNombrePastel(nombres[i]);
                        item.setCantidad(cantidades[i]);
                        item.setPrecioUnitario(precios[i]);
                        // HTML checkbox hack: since it might only send if checked, and we might use a hidden field
                        // but actually we expect "true" for those checked
                        // In multiple row forms this can be tricky, assuming aligned arrays from JS
                        boolean aCosto = "true".equals(costoStrings != null && costoStrings.length > i ? costoStrings[i] : "false");
                        item.setVendidoACosto(aCosto);
                        items.add(item);
                    }
                }
            }
            request.setItems(items);

            ventaService.crearVenta(request);
            redirectAttributes.addFlashAttribute("mensaje", "Venta registrada con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar la venta: " + e.getMessage());
            return "redirect:/web/ventas/nueva";
        }
        return "redirect:/web/ventas";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("venta", ventaService.obtenerPorId(id));
            return "ventas/detalle";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar la venta: " + e.getMessage());
            return "redirect:/web/ventas";
        }
    }

    @GetMapping("/{id}/cancelar")
    public String cancelarVenta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ventaService.cancelarVenta(id);
            redirectAttributes.addFlashAttribute("mensaje", "Venta cancelada con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar la venta: " + e.getMessage());
        }
        return "redirect:/web/ventas";
    }

    @GetMapping("/resumen/diario")
    public String resumenDiario(@RequestParam(required = false) String fecha, Model model) {
        try {
            LocalDate dateToUse = (fecha != null && !fecha.isEmpty()) ? LocalDate.parse(fecha) : LocalDate.now();
            model.addAttribute("fechaSeleccionada", dateToUse);
            model.addAttribute("total", ventaService.obtenerTotalDiario(dateToUse));
            model.addAttribute("cantidad", ventaService.obtenerCantidadVentasDiarias(dateToUse));
            // Just for the list below, we could add all ventas and filter in view or get them from a service.
            // But since VentaService has no findByFecha methods on interface, we can just load all or let them filter list by themselves.
            // Oh wait, VentaRepository has findByFechaVentaBetween, but not service. We can filter the list in the controller.
            model.addAttribute("ventas", ventaService.listarTodas().stream()
                    .filter(v -> v.getFechaVenta().toLocalDate().equals(dateToUse))
                    .toList());
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el resumen diario: " + e.getMessage());
        }
        return "ventas/resumen_diario";
    }

    @GetMapping("/resumen/mensual")
    public String resumenMensual(@RequestParam(required = false) Integer anio, Model model) {
        try {
            int yearToUse = (anio != null) ? anio : LocalDate.now().getYear();
            model.addAttribute("anioSeleccionado", yearToUse);
            model.addAttribute("resumen", ventaService.obtenerResumenMensual(yearToUse));
            model.addAttribute("totalAnual", ventaService.obtenerResumenMensual(yearToUse).values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el resumen mensual: " + e.getMessage());
        }
        return "ventas/resumen_mensual";
    }

    @GetMapping("/resumen/anual")
    public String resumenAnual(Model model) {
        try {
            model.addAttribute("resumen", ventaService.obtenerResumenAnual());
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el resumen anual: " + e.getMessage());
        }
        return "ventas/resumen_anual";
    }

    @GetMapping("/estadisticas")
    public String estadisticas(Model model) {
        try {
            model.addAttribute("productos", ventaService.obtenerProductosMasVendidos());
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las estadísticas: " + e.getMessage());
        }
        return "ventas/estadisticas";
    }
}
