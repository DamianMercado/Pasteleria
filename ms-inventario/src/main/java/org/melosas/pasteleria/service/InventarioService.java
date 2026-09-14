package org.melosas.pasteleria.service;

import org.melosas.pasteleria.dto.AjusteStockDTO;
import org.melosas.pasteleria.dto.InventarioItemRequestDTO;
import org.melosas.pasteleria.dto.InventarioItemResponseDTO;
import org.melosas.pasteleria.model.MovimientoInventario;

import java.util.List;

public interface InventarioService {
    List<InventarioItemResponseDTO> listarTodos();
    InventarioItemResponseDTO obtenerPorId(Long id);
    InventarioItemResponseDTO obtenerPorCodigo(String codigoPastel);
    Integer obtenerStockPorCodigo(String codigoPastel);
    InventarioItemResponseDTO crear(InventarioItemRequestDTO dto);
    InventarioItemResponseDTO actualizar(Long id, InventarioItemRequestDTO dto);
    InventarioItemResponseDTO descontarStock(String codigoPastel, Integer cantidad);
    InventarioItemResponseDTO descontarStockCosto(String codigoPastel, Integer cantidad);
    InventarioItemResponseDTO ajustarStock(Long id, AjusteStockDTO dto);
    List<MovimientoInventario> obtenerMovimientos(String codigoPastel);
    void eliminar(Long id);
}
