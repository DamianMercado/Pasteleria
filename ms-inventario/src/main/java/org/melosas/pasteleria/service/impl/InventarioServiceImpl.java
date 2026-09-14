package org.melosas.pasteleria.service.impl;

import lombok.RequiredArgsConstructor;
import org.melosas.pasteleria.dto.AjusteStockDTO;
import org.melosas.pasteleria.dto.InventarioItemRequestDTO;
import org.melosas.pasteleria.dto.InventarioItemResponseDTO;
import org.melosas.pasteleria.enums.TipoMovimiento;
import org.melosas.pasteleria.exception.BusinessException;
import org.melosas.pasteleria.exception.ResourceNotFoundException;
import org.melosas.pasteleria.mapper.InventarioMapper;
import org.melosas.pasteleria.model.InventarioItem;
import org.melosas.pasteleria.model.MovimientoInventario;
import org.melosas.pasteleria.repository.InventarioItemRepository;
import org.melosas.pasteleria.repository.MovimientoInventarioRepository;
import org.melosas.pasteleria.service.InventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {
    private final InventarioItemRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final InventarioMapper mapper;

    @Override
    public List<InventarioItemResponseDTO> listarTodos() {
        return inventarioRepository.findAll().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventarioItemResponseDTO obtenerPorId(Long id) {
        return inventarioRepository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de inventario no encontrado"));
    }

    @Override
    public InventarioItemResponseDTO obtenerPorCodigo(String codigoPastel) {
        return inventarioRepository.findByCodigoPastel(codigoPastel)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de inventario no encontrado"));
    }

    @Override
    public Integer obtenerStockPorCodigo(String codigoPastel) {
        InventarioItem item = inventarioRepository.findByCodigoPastel(codigoPastel)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de inventario no encontrado"));
        return item.getStock();
    }

    @Override
    @Transactional
    public InventarioItemResponseDTO crear(InventarioItemRequestDTO dto) {
        if (inventarioRepository.existsByCodigoPastel(dto.getCodigoPastel())) {
            throw new BusinessException("Ya existe un ítem con el código: " + dto.getCodigoPastel());
        }
        InventarioItem item = mapper.toEntity(dto);
        item.setVentaCosto(0);
        item = inventarioRepository.save(item);
        registrarMovimiento(item.getCodigoPastel(), TipoMovimiento.ENTRADA, item.getStock(), "Creación de ítem");
        return mapper.toResponseDTO(item);
    }

    @Override
    @Transactional
    public InventarioItemResponseDTO actualizar(Long id, InventarioItemRequestDTO dto) {
        InventarioItem item = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de inventario no encontrado"));
        
        if (!item.getCodigoPastel().equals(dto.getCodigoPastel()) && 
            inventarioRepository.existsByCodigoPastel(dto.getCodigoPastel())) {
            throw new BusinessException("Ya existe un ítem con el código: " + dto.getCodigoPastel());
        }

        Integer stockAnterior = item.getStock();
        item.setCodigoPastel(dto.getCodigoPastel());
        item.setNombrePastel(dto.getNombrePastel());
        item.setStock(dto.getStock());
        item.setCompraId(dto.getCompraId());
        item = inventarioRepository.save(item);

        if (!stockAnterior.equals(item.getStock())) {
            registrarMovimiento(item.getCodigoPastel(), TipoMovimiento.AJUSTE, item.getStock() - stockAnterior, "Actualización manual de ítem");
        }

        return mapper.toResponseDTO(item);
    }

    @Override
    @Transactional
    public InventarioItemResponseDTO descontarStock(String codigoPastel, Integer cantidad) {
        InventarioItem item = inventarioRepository.findByCodigoPastel(codigoPastel)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de inventario no encontrado"));
        
        if (item.getStock() < cantidad) {
            throw new BusinessException("Stock insuficiente para el código: " + codigoPastel);
        }
        
        item.setStock(item.getStock() - cantidad);
        item = inventarioRepository.save(item);
        registrarMovimiento(codigoPastel, TipoMovimiento.VENTA, cantidad, "Venta normal");
        return mapper.toResponseDTO(item);
    }

    @Override
    @Transactional
    public InventarioItemResponseDTO descontarStockCosto(String codigoPastel, Integer cantidad) {
        InventarioItem item = inventarioRepository.findByCodigoPastel(codigoPastel)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de inventario no encontrado"));
        
        if (item.getStock() < cantidad) {
            throw new BusinessException("Stock insuficiente para el código: " + codigoPastel);
        }
        
        item.setStock(item.getStock() - cantidad);
        item.setVentaCosto(item.getVentaCosto() + cantidad);
        item = inventarioRepository.save(item);
        registrarMovimiento(codigoPastel, TipoMovimiento.VENTA_COSTO, cantidad, "Venta a costo");
        return mapper.toResponseDTO(item);
    }

    @Override
    @Transactional
    public InventarioItemResponseDTO ajustarStock(Long id, AjusteStockDTO dto) {
        InventarioItem item = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de inventario no encontrado"));
        
        item.setStock(dto.getCantidad());
        item = inventarioRepository.save(item);
        registrarMovimiento(item.getCodigoPastel(), TipoMovimiento.AJUSTE, dto.getCantidad(), dto.getMotivo());
        return mapper.toResponseDTO(item);
    }

    @Override
    public List<MovimientoInventario> obtenerMovimientos(String codigoPastel) {
        return movimientoRepository.findByCodigoPastelOrderByFechaMovimientoDesc(codigoPastel);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        InventarioItem item = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de inventario no encontrado"));
        inventarioRepository.delete(item);
    }

    private void registrarMovimiento(String codigoPastel, TipoMovimiento tipo, Integer cantidad, String motivo) {
        MovimientoInventario mov = MovimientoInventario.builder()
                .codigoPastel(codigoPastel)
                .tipoMovimiento(tipo)
                .cantidad(cantidad)
                .motivo(motivo)
                .fechaMovimiento(LocalDateTime.now())
                .build();
        movimientoRepository.save(mov);
    }
}
