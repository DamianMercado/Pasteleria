package org.melosas.pasteleria.service;

import org.melosas.pasteleria.dto.PastelRequestDTO;
import org.melosas.pasteleria.dto.PastelResponseDTO;

import java.util.List;

public interface PastelService {
    List<PastelResponseDTO> listarTodos();
    PastelResponseDTO obtenerPorId(Long id);
    PastelResponseDTO obtenerPorCodigo(String codigoPastel);
    List<PastelResponseDTO> listarPorCompraId(Long compraId);
    PastelResponseDTO crear(PastelRequestDTO dto);
    PastelResponseDTO actualizar(Long id, PastelRequestDTO dto);
    void eliminar(Long id);
}
