package org.melosas.pasteleria.service;

import org.melosas.pasteleria.dto.PagoRequestDTO;
import org.melosas.pasteleria.dto.PagoResponseDTO;

import java.util.List;

public interface PagoService {
    List<PagoResponseDTO> listarTodos();
    PagoResponseDTO obtenerPorId(Long id);
    List<PagoResponseDTO> obtenerPorVentaId(Long ventaId);
    List<PagoResponseDTO> listarFiadosPendientes();
    PagoResponseDTO procesarPago(PagoRequestDTO dto);
    PagoResponseDTO marcarComoPagado(Long id);
    void anularPago(Long id);
}
