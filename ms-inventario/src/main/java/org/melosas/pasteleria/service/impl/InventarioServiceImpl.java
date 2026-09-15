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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {
    private final InventarioItemRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final InventarioMapper mapper;

    @Override
    public List<InventarioItemResponseDTO> listarTodos() {
        Map<String, Integer> stockTotalPorCodigo = new HashMap<>();
        List<InventarioItem> items = inventarioRepository.findAll();
        for (InventarioItem item : items) {
            stockTotalPorCodigo.merge(item.getCodigoPastel(), item.getStock(), Integer::sum);
        }

        return items.stream()
                .map(item -> {
                    InventarioItemResponseDTO dto = mapper.toResponseDTO(item);
                    dto.setStockTotalProducto(stockTotalPorCodigo.getOrDefault(item.getCodigoPastel(), item.getStock()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public InventarioItemResponseDTO obtenerPorId(Long id) {
        InventarioItem item = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de inventario no encontrado"));
        InventarioItemResponseDTO dto = mapper.toResponseDTO(item);
        dto.setStockTotalProducto(inventarioRepository.sumStockByCodigoPastel(item.getCodigoPastel()));
        return dto;
    }

    @Override
    public InventarioItemResponseDTO obtenerPorCodigo(String codigoPastel) {
        List<InventarioItem> lotes = inventarioRepository.findByCodigoPastel(codigoPastel);
        if (lotes.isEmpty()) {
            throw new ResourceNotFoundException("Ítem de inventario no encontrado para código: " + codigoPastel);
        }
        InventarioItem primer = lotes.get(0);
        Integer stockTotal = inventarioRepository.sumStockByCodigoPastel(codigoPastel);

        InventarioItemResponseDTO dto = mapper.toResponseDTO(primer);
        dto.setStock(stockTotal);
        dto.setStockTotalProducto(stockTotal);

        LocalDate hoy = LocalDate.now();
        boolean tieneVencidos = lotes.stream()
                .anyMatch(l -> l.getFechaVencimiento() != null && l.getFechaVencimiento().isBefore(hoy) && l.getStock() > 0);
        dto.setVencido(tieneVencidos);

        return dto;
    }

    @Override
    public Integer obtenerStockPorCodigo(String codigoPastel) {
        Integer stock = inventarioRepository.sumStockByCodigoPastel(codigoPastel);
        return stock != null ? stock : 0;
    }

    @Override
    @Transactional
    public InventarioItemResponseDTO crear(InventarioItemRequestDTO dto) {
        if (dto.getCompraId() != null && inventarioRepository.existsByCodigoPastelAndCompraId(dto.getCodigoPastel(), dto.getCompraId())) {
            throw new BusinessException("Ya existe un ítem registrado con el código '" + dto.getCodigoPastel() + "' para la compra #" + dto.getCompraId());
        }

        if (dto.getNombrePastel() == null || dto.getNombrePastel().trim().isEmpty()) {
            inventarioRepository.findFirstByCodigoPastel(dto.getCodigoPastel())
                    .ifPresent(existente -> dto.setNombrePastel(existente.getNombrePastel()));
        }

        InventarioItem item = mapper.toEntity(dto);
        item.setVentaCosto(0);
        item = inventarioRepository.save(item);

        registrarMovimiento(item.getCodigoPastel(), TipoMovimiento.ENTRADA, item.getStock(), 
                "Creación de lote - Compra #" + (item.getCompraId() != null ? item.getCompraId() : "N/A"));

        InventarioItemResponseDTO response = mapper.toResponseDTO(item);
        response.setStockTotalProducto(inventarioRepository.sumStockByCodigoPastel(item.getCodigoPastel()));
        return response;
    }

    @Override
    @Transactional
    public InventarioItemResponseDTO actualizar(Long id, InventarioItemRequestDTO dto) {
        InventarioItem item = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de inventario no encontrado"));

        if ((!item.getCodigoPastel().equals(dto.getCodigoPastel()) || 
             (item.getCompraId() != null && !item.getCompraId().equals(dto.getCompraId()))) &&
            inventarioRepository.existsByCodigoPastelAndCompraId(dto.getCodigoPastel(), dto.getCompraId())) {
            throw new BusinessException("Ya existe un ítem con el código '" + dto.getCodigoPastel() + "' para la compra #" + dto.getCompraId());
        }

        Integer stockAnterior = item.getStock();
        item.setCodigoPastel(dto.getCodigoPastel());
        item.setNombrePastel(dto.getNombrePastel());
        item.setStock(dto.getStock());
        item.setCompraId(dto.getCompraId());
        item.setFechaVencimiento(dto.getFechaVencimiento());
        item = inventarioRepository.save(item);

        if (!stockAnterior.equals(item.getStock())) {
            registrarMovimiento(item.getCodigoPastel(), TipoMovimiento.AJUSTE, item.getStock() - stockAnterior, "Actualización manual de ítem");
        }

        InventarioItemResponseDTO response = mapper.toResponseDTO(item);
        response.setStockTotalProducto(inventarioRepository.sumStockByCodigoPastel(item.getCodigoPastel()));
        return response;
    }

    @Override
    @Transactional
    public InventarioItemResponseDTO descontarStock(String codigoPastel, Integer cantidad) {
        Integer stockTotal = inventarioRepository.sumStockByCodigoPastel(codigoPastel);
        if (stockTotal == null || stockTotal < cantidad) {
            throw new BusinessException("Stock insuficiente para el código: " + codigoPastel + ". Disponible: " + (stockTotal != null ? stockTotal : 0) + ", Solicitado: " + cantidad);
        }

        List<InventarioItem> lotes = inventarioRepository.findByCodigoPastelOrderByFechaVencimientoAsc(codigoPastel);
        if (lotes.isEmpty()) {
            throw new ResourceNotFoundException("No hay lotes en inventario para el código: " + codigoPastel);
        }

        LocalDate hoy = LocalDate.now();
        List<InventarioItem> ordenDescuento = new ArrayList<>(
                lotes.stream()
                        .filter(l -> (l.getFechaVencimiento() == null || !l.getFechaVencimiento().isBefore(hoy)) && l.getStock() > 0)
                        .toList()
        );

        for (InventarioItem l : lotes) {
            if (!ordenDescuento.contains(l) && l.getStock() > 0) {
                ordenDescuento.add(l);
            }
        }

        int restante = cantidad;
        InventarioItem ultimoAfectado = null;
        for (InventarioItem lote : ordenDescuento) {
            if (restante <= 0) break;
            int aDescontar = Math.min(lote.getStock(), restante);
            lote.setStock(lote.getStock() - aDescontar);
            restante -= aDescontar;
            inventarioRepository.save(lote);
            ultimoAfectado = lote;
        }

        registrarMovimiento(codigoPastel, TipoMovimiento.VENTA, cantidad, "Venta normal");

        InventarioItemResponseDTO response = mapper.toResponseDTO(ultimoAfectado != null ? ultimoAfectado : lotes.get(0));
        Integer nuevoTotal = inventarioRepository.sumStockByCodigoPastel(codigoPastel);
        response.setStock(nuevoTotal);
        response.setStockTotalProducto(nuevoTotal);
        return response;
    }

    @Override
    @Transactional
    public InventarioItemResponseDTO descontarStockCosto(String codigoPastel, Integer cantidad) {
        List<InventarioItem> lotes = inventarioRepository.findByCodigoPastelOrderByFechaVencimientoAsc(codigoPastel);
        LocalDate hoy = LocalDate.now();

        List<InventarioItem> lotesVencidos = lotes.stream()
                .filter(l -> l.getFechaVencimiento() != null && l.getFechaVencimiento().isBefore(hoy) && l.getStock() > 0)
                .toList();

        int stockVencidoTotal = lotesVencidos.stream().mapToInt(InventarioItem::getStock).sum();
        if (stockVencidoTotal < cantidad) {
            throw new BusinessException("Stock vencido insuficiente para venta a costo del código: " + codigoPastel + ". Disponible vencido: " + stockVencidoTotal + ", Solicitado: " + cantidad);
        }

        int restante = cantidad;
        InventarioItem ultimoAfectado = null;
        for (InventarioItem lote : lotesVencidos) {
            if (restante <= 0) break;
            int aDescontar = Math.min(lote.getStock(), restante);
            lote.setStock(lote.getStock() - aDescontar);
            lote.setVentaCosto(lote.getVentaCosto() + aDescontar);
            restante -= aDescontar;
            inventarioRepository.save(lote);
            ultimoAfectado = lote;
        }

        registrarMovimiento(codigoPastel, TipoMovimiento.VENTA_COSTO, cantidad, "Venta a costo");

        InventarioItemResponseDTO response = mapper.toResponseDTO(ultimoAfectado != null ? ultimoAfectado : lotes.get(0));
        Integer nuevoTotal = inventarioRepository.sumStockByCodigoPastel(codigoPastel);
        response.setStock(nuevoTotal);
        response.setStockTotalProducto(nuevoTotal);
        return response;
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
