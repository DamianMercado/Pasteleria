package org.melosas.pasteleria.service.impl;

import lombok.RequiredArgsConstructor;
import org.melosas.pasteleria.dto.ActualizarEstadoCompraDTO;
import org.melosas.pasteleria.dto.CompraRequestDTO;
import org.melosas.pasteleria.dto.CompraResponseDTO;
import org.melosas.pasteleria.enums.EstadoCompra;
import org.melosas.pasteleria.exception.ResourceNotFoundException;
import org.melosas.pasteleria.mapper.CompraMapper;
import org.melosas.pasteleria.model.Compra;
import org.melosas.pasteleria.repository.CompraRepository;
import org.melosas.pasteleria.service.CompraService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final CompraMapper compraMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CompraResponseDTO> listarTodas() {
        return compraRepository.findAll().stream()
                .map(compraMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompraResponseDTO obtenerPorId(Long id) {
        Compra compra = getCompraById(id);
        return compraMapper.toResponseDTO(compra);
    }

    @Override
    @Transactional
    public CompraResponseDTO crearOrdenCompra(CompraRequestDTO dto) {
        Compra compra = compraMapper.toEntity(dto);
        Compra saved = compraRepository.save(compra);
        return compraMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public CompraResponseDTO actualizarEstado(Long id, ActualizarEstadoCompraDTO dto) {
        Compra compra = getCompraById(id);
        compra.setEstadoCompra(dto.getNuevoEstado());
        Compra saved = compraRepository.save(compra);
        return compraMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void cancelarCompra(Long id) {
        Compra compra = getCompraById(id);
        compra.setEstadoCompra(EstadoCompra.CANCELADA);
        compraRepository.save(compra);
    }
    
    private Compra getCompraById(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + id));
    }
}
