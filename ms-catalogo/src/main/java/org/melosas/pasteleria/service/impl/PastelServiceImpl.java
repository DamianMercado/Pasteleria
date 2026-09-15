package org.melosas.pasteleria.service.impl;

import lombok.RequiredArgsConstructor;
import org.melosas.pasteleria.client.InventarioClient;
import org.melosas.pasteleria.dto.PastelRequestDTO;
import org.melosas.pasteleria.dto.PastelResponseDTO;
import org.melosas.pasteleria.exception.BusinessException;
import org.melosas.pasteleria.exception.ResourceNotFoundException;
import org.melosas.pasteleria.mapper.PastelMapper;
import org.melosas.pasteleria.model.Pastel;
import org.melosas.pasteleria.repository.PastelRepository;
import org.melosas.pasteleria.service.PastelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PastelServiceImpl implements PastelService {

    private final PastelRepository pastelRepository;
    private final PastelMapper pastelMapper;
    private final InventarioClient inventarioClient;

    @Override
    @Transactional(readOnly = true)
    public List<PastelResponseDTO> listarTodos() {
        return pastelRepository.findAll().stream()
                .map(pastel -> {
                    PastelResponseDTO dto = pastelMapper.toResponseDTO(pastel);
                    cargarStock(dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PastelResponseDTO obtenerPorId(Long id) {
        Pastel pastel = pastelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pastel no encontrado con ID: " + id));
        PastelResponseDTO dto = pastelMapper.toResponseDTO(pastel);
        cargarStock(dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public PastelResponseDTO obtenerPorCodigo(String codigoPastel) {
        Pastel pastel = pastelRepository.findByCodigoPastel(codigoPastel)
                .orElseThrow(() -> new ResourceNotFoundException("Pastel no encontrado con código: " + codigoPastel));
        PastelResponseDTO dto = pastelMapper.toResponseDTO(pastel);
        cargarStock(dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PastelResponseDTO> listarPorCompraId(Long compraId) {
        return pastelRepository.findByCompraId(compraId).stream()
                .map(pastel -> {
                    PastelResponseDTO dto = pastelMapper.toResponseDTO(pastel);
                    cargarStock(dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PastelResponseDTO crear(PastelRequestDTO dto) {
        if (pastelRepository.existsByCodigoPastel(dto.getCodigoPastel())) {
            throw new BusinessException("El código del pastel ya existe: " + dto.getCodigoPastel());
        }
        if (dto.getNombrePastel() == null || dto.getNombrePastel().trim().isEmpty()) {
            dto.setNombrePastel(dto.getCodigoPastel());
        }
        Pastel pastel = pastelMapper.toEntity(dto);
        pastel = pastelRepository.save(pastel);
        return pastelMapper.toResponseDTO(pastel);
    }

    @Override
    @Transactional
    public PastelResponseDTO actualizar(Long id, PastelRequestDTO dto) {
        Pastel pastel = pastelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pastel no encontrado con ID: " + id));
        
        if (!pastel.getCodigoPastel().equals(dto.getCodigoPastel()) && 
            pastelRepository.existsByCodigoPastel(dto.getCodigoPastel())) {
            throw new BusinessException("El código del pastel ya existe: " + dto.getCodigoPastel());
        }
        if (dto.getNombrePastel() == null || dto.getNombrePastel().trim().isEmpty()) {
            dto.setNombrePastel(dto.getCodigoPastel());
        }
        pastelMapper.updateEntityFromDTO(dto, pastel);
        pastel = pastelRepository.save(pastel);
        return pastelMapper.toResponseDTO(pastel);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!pastelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pastel no encontrado con ID: " + id);
        }
        pastelRepository.deleteById(id);
    }

    private void cargarStock(PastelResponseDTO dto) {
        try {
            Integer stock = inventarioClient.obtenerStock(dto.getCodigoPastel());
            dto.setStock(stock);
            if (stock == null) {
                dto.setEstadoStock("N/A");
            } else if (stock > 0) {
                dto.setEstadoStock("Con stock (" + stock + ")");
            } else {
                dto.setEstadoStock("Sin stock");
            }
        } catch (Exception e) {
            dto.setStock(null);
            dto.setEstadoStock("N/A");
        }
    }
}
