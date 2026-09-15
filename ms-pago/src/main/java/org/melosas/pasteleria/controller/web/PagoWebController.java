package org.melosas.pasteleria.controller.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.melosas.pasteleria.dto.PagoRequestDTO;
import org.melosas.pasteleria.dto.PagoResponseDTO;
import org.melosas.pasteleria.enums.MetodoPago;
import org.melosas.pasteleria.service.PagoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/web/pagos")
@RequiredArgsConstructor
public class PagoWebController {

    private final PagoService pagoService;
    private final org.melosas.pasteleria.client.VentaClient ventaClient;

    @GetMapping
    public String listarPagos(Model model) {
        try {
            model.addAttribute("pagos", pagoService.listarTodos());
        } catch (Exception e) {
            model.addAttribute("error", "Error al obtener los pagos: " + e.getMessage());
        }
        return "pagos/lista";
    }

    @GetMapping("/{id}/editar")
    public String editarPago(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            PagoResponseDTO pago = pagoService.obtenerPorId(id);
            PagoRequestDTO dto = PagoRequestDTO.builder()
                    .ventaId(pago.getVentaId())
                    .clienteNombre(pago.getClienteNombre())
                    .monto(pago.getMonto())
                    .metodoPago(pago.getMetodoPago())
                    .estadoPago(pago.getEstadoPago())
                    .fechaVencimiento(pago.getFechaVencimiento())
                    .notas(pago.getNotas())
                    .build();

            model.addAttribute("pagoId", id);
            model.addAttribute("pago", dto);
            model.addAttribute("pagoOriginal", pago);
            model.addAttribute("metodosPago", MetodoPago.values());
            model.addAttribute("estadosPago", org.melosas.pasteleria.enums.EstadoPago.values());

            if (pago.getVentaId() != null) {
                try {
                    org.melosas.pasteleria.client.VentaClient.VentaDTO venta = ventaClient.obtenerVenta(pago.getVentaId());
                    model.addAttribute("venta", venta);
                } catch (Exception ex) {
                    model.addAttribute("ventaError", "No se pudo consultar el detalle de venta en ms-venta: " + ex.getMessage());
                }
            }

            return "pagos/formulario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al obtener el pago: " + e.getMessage());
            return "redirect:/web/pagos";
        }
    }

    @PostMapping("/{id}/editar")
    public String actualizarPago(@PathVariable Long id,
                                 @Valid @ModelAttribute("pago") PagoRequestDTO pagoRequestDTO,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("pagoId", id);
            model.addAttribute("metodosPago", MetodoPago.values());
            model.addAttribute("estadosPago", org.melosas.pasteleria.enums.EstadoPago.values());
            if (pagoRequestDTO.getVentaId() != null) {
                try {
                    org.melosas.pasteleria.client.VentaClient.VentaDTO venta = ventaClient.obtenerVenta(pagoRequestDTO.getVentaId());
                    model.addAttribute("venta", venta);
                } catch (Exception ignored) {}
            }
            return "pagos/formulario";
        }

        try {
            pagoService.actualizarPago(id, pagoRequestDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Pago actualizado exitosamente.");
            return "redirect:/web/pagos";
        } catch (Exception e) {
            model.addAttribute("pagoId", id);
            model.addAttribute("error", "Error al actualizar el pago: " + e.getMessage());
            model.addAttribute("metodosPago", MetodoPago.values());
            model.addAttribute("estadosPago", org.melosas.pasteleria.enums.EstadoPago.values());
            if (pagoRequestDTO.getVentaId() != null) {
                try {
                    org.melosas.pasteleria.client.VentaClient.VentaDTO venta = ventaClient.obtenerVenta(pagoRequestDTO.getVentaId());
                    model.addAttribute("venta", venta);
                } catch (Exception ignored) {}
            }
            return "pagos/formulario";
        }
    }

    @GetMapping("/{id}")
    public String verComprobante(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("pago", pagoService.obtenerPorId(id));
            return "pagos/comprobante";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al obtener el pago: " + e.getMessage());
            return "redirect:/web/pagos";
        }
    }

    @GetMapping("/fiados")
    public String listarFiados(Model model) {
        try {
            List<PagoResponseDTO> fiados = pagoService.listarFiadosPendientes();
            Map<Long, Long> diasRestantes = new HashMap<>();
            LocalDateTime now = LocalDateTime.now();
            
            for (PagoResponseDTO fiado : fiados) {
                if (fiado.getFechaVencimiento() != null) {
                    long days = ChronoUnit.DAYS.between(now, fiado.getFechaVencimiento());
                    diasRestantes.put(fiado.getId(), days);
                }
            }
            
            model.addAttribute("fiados", fiados);
            model.addAttribute("diasRestantes", diasRestantes);
        } catch (Exception e) {
            model.addAttribute("error", "Error al obtener los fiados pendientes: " + e.getMessage());
        }
        return "pagos/fiados";
    }

    @GetMapping("/{id}/pagar")
    public String marcarComoPagado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pagoService.marcarComoPagado(id);
            redirectAttributes.addFlashAttribute("mensaje", "Pago marcado como pagado exitosamente.");
            return "redirect:/web/pagos/fiados";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el pago: " + e.getMessage());
            return "redirect:/web/pagos/fiados";
        }
    }

    @GetMapping("/{id}/anular")
    public String anularPago(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pagoService.anularPago(id);
            redirectAttributes.addFlashAttribute("mensaje", "Pago anulado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al anular el pago: " + e.getMessage());
        }
        return "redirect:/web/pagos";
    }
}
