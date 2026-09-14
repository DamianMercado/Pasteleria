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

    @GetMapping
    public String listarPagos(Model model) {
        try {
            model.addAttribute("pagos", pagoService.listarTodos());
        } catch (Exception e) {
            model.addAttribute("error", "Error al obtener los pagos: " + e.getMessage());
        }
        return "pagos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoPago(Model model) {
        model.addAttribute("pago", new PagoRequestDTO());
        model.addAttribute("metodosPago", MetodoPago.values());
        return "pagos/formulario";
    }

    @PostMapping("/guardar")
    public String procesarPago(@Valid @ModelAttribute("pago") PagoRequestDTO pagoRequestDTO,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("metodosPago", MetodoPago.values());
            return "pagos/formulario";
        }

        try {
            pagoService.procesarPago(pagoRequestDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Pago procesado exitosamente.");
            return "redirect:/web/pagos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al procesar el pago: " + e.getMessage());
            model.addAttribute("metodosPago", MetodoPago.values());
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
