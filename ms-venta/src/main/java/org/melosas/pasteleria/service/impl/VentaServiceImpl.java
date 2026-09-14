package org.melosas.pasteleria.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.melosas.pasteleria.client.InventarioClient;
import org.melosas.pasteleria.dto.DetalleVentaDTO;
import org.melosas.pasteleria.dto.VentaRequestDTO;
import org.melosas.pasteleria.dto.VentaResponseDTO;
import org.melosas.pasteleria.enums.EstadoVenta;
import org.melosas.pasteleria.exception.BusinessException;
import org.melosas.pasteleria.exception.ResourceNotFoundException;
import org.melosas.pasteleria.mapper.VentaMapper;
import org.melosas.pasteleria.model.Venta;
import org.melosas.pasteleria.repository.DetalleVentaRepository;
import org.melosas.pasteleria.repository.VentaRepository;
import org.melosas.pasteleria.service.VentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final VentaMapper ventaMapper;
    private final InventarioClient inventarioClient;

    @Override
    public List<VentaResponseDTO> listarTodas() {
        return ventaRepository.findAll().stream()
                .map(ventaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VentaResponseDTO obtenerPorId(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + id));
        return ventaMapper.toResponseDTO(venta);
    }

    @Override
    @Transactional
    public VentaResponseDTO crearVenta(VentaRequestDTO dto) {
        Venta venta = ventaMapper.toEntity(dto);

        if (dto.getItems() != null) {
            for (DetalleVentaDTO item : dto.getItems()) {
                try {
                    if (Boolean.TRUE.equals(item.getVendidoACosto())) {
                        inventarioClient.descontarStockCosto(item.getCodigoPastel(), item.getCantidad());
                    } else {
                        inventarioClient.descontarStock(item.getCodigoPastel(), item.getCantidad());
                    }
                } catch (BusinessException e) {
                    throw e; // propagate
                } catch (Exception e) {
                    log.warn("Error de comunicación con ms-inventario al descontar stock de {}", item.getCodigoPastel(), e);
                }
            }
        }

        venta = ventaRepository.save(venta);
        return ventaMapper.toResponseDTO(venta);
    }

    @Override
    @Transactional
    public void cancelarVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + id));
        venta.setEstadoVenta(EstadoVenta.CANCELADA);
        ventaRepository.save(venta);
    }

    @Override
    public BigDecimal obtenerTotalDiario(LocalDate fecha) {
        LocalDateTime start = fecha.atStartOfDay();
        LocalDateTime end = fecha.plusDays(1).atStartOfDay();
        BigDecimal total = ventaRepository.sumTotalVentaByFechaBetween(start, end);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public Long obtenerCantidadVentasDiarias(LocalDate fecha) {
        LocalDateTime start = fecha.atStartOfDay();
        LocalDateTime end = fecha.plusDays(1).atStartOfDay();
        return ventaRepository.countVentasByFechaBetween(start, end);
    }

    @Override
    public Map<Integer, BigDecimal> obtenerResumenMensual(int anio) {
        Map<Integer, BigDecimal> resumen = new HashMap<>();
        for (int mes = 1; mes <= 12; mes++) {
            LocalDateTime start = LocalDateTime.of(anio, mes, 1, 0, 0);
            LocalDateTime end = start.plusMonths(1);
            BigDecimal total = ventaRepository.sumTotalVentaByFechaBetween(start, end);
            resumen.put(mes, total != null ? total : BigDecimal.ZERO);
        }
        return resumen;
    }

    @Override
    public Map<Integer, BigDecimal> obtenerResumenAnual() {
        List<Venta> ventas = ventaRepository.findByEstadoVenta(EstadoVenta.COMPLETADA);
        Map<Integer, BigDecimal> resumen = new HashMap<>();
        for (Venta v : ventas) {
            int year = v.getFechaVenta().getYear();
            resumen.merge(year, v.getTotalVenta(), BigDecimal::add);
        }
        return resumen;
    }

    @Override
    public List<Map<String, Object>> obtenerProductosMasVendidos() {
        List<Object[]> results = detalleVentaRepository.findProductosMasVendidos();
        List<Map<String, Object>> mapResults = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("codigoPastel", row[0]);
            map.put("nombrePastel", row[1]);
            map.put("totalVendido", row[2]);
            mapResults.add(map);
        }
        return mapResults;
    }
}
