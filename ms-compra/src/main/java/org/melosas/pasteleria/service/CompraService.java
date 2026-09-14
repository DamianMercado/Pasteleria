package org.melosas.pasteleria.service;

import org.melosas.pasteleria.dto.ActualizarEstadoCompraDTO;
import org.melosas.pasteleria.dto.CompraRequestDTO;
import org.melosas.pasteleria.dto.CompraResponseDTO;

import java.util.List;

public interface CompraService {
    List<CompraResponseDTO> listarTodas();
    CompraResponseDTO obtenerPorId(Long id);
    CompraResponseDTO crearOrdenCompra(CompraRequestDTO dto);
    CompraResponseDTO actualizarEstado(Long id, ActualizarEstadoCompraDTO dto);
    void cancelarCompra(Long id);
}
