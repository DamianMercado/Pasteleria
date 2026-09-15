package org.melosas.pasteleria.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.melosas.pasteleria.client.CatalogoClient;
import org.melosas.pasteleria.client.CompraClient;
import org.melosas.pasteleria.client.InventarioClient;
import org.melosas.pasteleria.client.PagoClient;
import org.melosas.pasteleria.dto.DetalleVentaDTO;
import org.melosas.pasteleria.dto.VentaRequestDTO;
import org.melosas.pasteleria.service.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/web/ventas")
@RequiredArgsConstructor
public class VentaWebController {

    private final VentaService ventaService;
    private final CompraClient compraClient;
    private final CatalogoClient catalogoClient;
    private final InventarioClient inventarioClient;
    private final PagoClient pagoClient;

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
        List<String> codigosCompra = new ArrayList<>();
        try {
            var compras = compraClient.listarCompras();
            if (compras != null) {
                for (var c : compras) {
                    if (c.getItems() != null) {
                        for (var it : c.getItems()) {
                            String cod = it.getCodigo();
                            if (cod != null && !cod.trim().isEmpty() && !codigosCompra.contains(cod.trim())) {
                                codigosCompra.add(cod.trim());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("No se pudieron cargar códigos desde compras: {}", e.getMessage());
        }
        model.addAttribute("codigosCompra", codigosCompra);
        return "ventas/formulario";
    }

    @GetMapping("/api/producto/{codigoPastel}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerInfoProducto(@PathVariable String codigoPastel) {
        Map<String, Object> data = new HashMap<>();
        data.put("codigoPastel", codigoPastel);
        data.put("nombrePastel", "");
        data.put("precioVenta", null);
        data.put("precioCosto", null);
        data.put("vencido", false);

        // 1. Inventario (para nombre de producto y stock)
        try {
            var inv = inventarioClient.obtenerPorCodigo(codigoPastel);
            if (inv != null) {
                if (inv.nombrePastel() != null) {
                    data.put("nombrePastel", inv.nombrePastel());
                }
                data.put("stock", inv.stock() != null ? inv.stock() : 0);
                if (Boolean.TRUE.equals(inv.vencido())) {
                    data.put("vencido", true);
                }
            }
        } catch (Exception e) {
            log.warn("Error al consultar ms-inventario para {}: {}", codigoPastel, e.getMessage());
        }

        // 2. Catalogo (para precios y fecha de vencimiento)
        try {
            var cat = catalogoClient.obtenerPorCodigo(codigoPastel);
            if (cat != null) {
                if (data.get("nombrePastel") == null || ((String) data.get("nombrePastel")).isEmpty()) {
                    data.put("nombrePastel", cat.nombrePastel());
                }
                data.put("precioVenta", cat.precioVenta());
                BigDecimal costo = cat.precioCosto() != null ? cat.precioCosto() : cat.precioPastel();
                data.put("precioCosto", costo);
                data.put("vencido", Boolean.TRUE.equals(cat.vencido()));
                data.put("fechaVencimiento", cat.fechaVencimiento());
            }
        } catch (Exception e) {
            log.warn("Error al consultar ms-catalogo para {}: {}", codigoPastel, e.getMessage());
        }

        return ResponseEntity.ok(data);
    }

    @PostMapping("/guardar")
    public String guardarVenta(
            @RequestParam String calle,
            @RequestParam String ciudad,
            @RequestParam(required = false) String clienteNombre,
            @RequestParam(required = false) String notas,
            @RequestParam(name = "metodoPago", defaultValue = "EFECTIVO") String metodoPago,
            @RequestParam(name = "fechaVencimientoPago", required = false) String fechaVencimientoPago,
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
                        boolean aCosto = "true".equalsIgnoreCase(costoStrings != null && costoStrings.length > i ? costoStrings[i] : "false");
                        item.setVendidoACosto(aCosto);
                        items.add(item);
                    }
                }
            }
            request.setItems(items);

            var venta = ventaService.crearVenta(request);

            // Generar pago automáticamente en ms-pago
            try {
                java.time.LocalDateTime vencimiento = null;
                if ("FIADO".equalsIgnoreCase(metodoPago) && fechaVencimientoPago != null && !fechaVencimientoPago.trim().isEmpty()) {
                    vencimiento = LocalDate.parse(fechaVencimientoPago).atTime(23, 59, 59);
                }
                String nombreClientePago = (clienteNombre != null && !clienteNombre.trim().isEmpty()) ? clienteNombre.trim() : "Cliente General";
                BigDecimal montoPago = venta.getTotalVenta() != null ? venta.getTotalVenta() : BigDecimal.ZERO;

                pagoClient.procesarPago(new PagoClient.PagoRequestDTO(
                    venta.getId(),
                    nombreClientePago,
                    montoPago,
                    metodoPago.toUpperCase(),
                    vencimiento,
                    "Generado automáticamente desde Venta #" + venta.getId()
                ));
                redirectAttributes.addFlashAttribute("mensaje", "Venta registrada con éxito y pago generado.");
            } catch (Exception e) {
                log.warn("Error al registrar pago automático para venta #{}: {}", venta.getId(), e.getMessage());
                redirectAttributes.addFlashAttribute("mensaje", "Venta registrada con éxito (atención: el pago no se pudo generar automáticamente: " + e.getMessage() + ")");
            }

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
