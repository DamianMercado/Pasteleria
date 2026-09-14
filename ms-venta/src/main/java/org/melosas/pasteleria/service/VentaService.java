package org.melosas.pasteleria.service;

import org.melosas.pasteleria.dto.VentaRequestDTO;
import org.melosas.pasteleria.dto.VentaResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface VentaService {
    List<VentaResponseDTO> listarTodas();
    VentaResponseDTO obtenerPorId(Long id);
    VentaResponseDTO crearVenta(VentaRequestDTO dto);
    void cancelarVenta(Long id);
    BigDecimal obtenerTotalDiario(LocalDate fecha);
    Long obtenerCantidadVentasDiarias(LocalDate fecha);
    Map<Integer, BigDecimal> obtenerResumenMensual(int anio);
    Map<Integer, BigDecimal> obtenerResumenAnual();
    List<Map<String, Object>> obtenerProductosMasVendidos();
}
