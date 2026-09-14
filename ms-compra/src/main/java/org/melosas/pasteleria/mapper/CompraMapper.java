package org.melosas.pasteleria.mapper;

import org.melosas.pasteleria.dto.CompraRequestDTO;
import org.melosas.pasteleria.dto.CompraResponseDTO;
import org.melosas.pasteleria.dto.DetalleCompraDTO;
import org.melosas.pasteleria.enums.EstadoCompra;
import org.melosas.pasteleria.model.Compra;
import org.melosas.pasteleria.model.DetalleCompra;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompraMapper {

    public Compra toEntity(CompraRequestDTO dto) {
        Compra compra = Compra.builder()
                .proveedorNombre(dto.getProveedorNombre())
                .proveedorContacto(dto.getProveedorContacto())
                .costoTransporte(dto.getCostoTransporte() != null ? dto.getCostoTransporte() : BigDecimal.ZERO)
                .notas(dto.getNotas())
                .fechaCompra(LocalDateTime.now())
                .estadoCompra(EstadoCompra.PENDIENTE)
                .gananciaObtenida(BigDecimal.ZERO)
                .build();

        List<DetalleCompra> items = new ArrayList<>();
        BigDecimal totalProductos = BigDecimal.ZERO;

        if (dto.getItems() != null) {
            for (DetalleCompraDTO itemDto : dto.getItems()) {
                DetalleCompra detalle = DetalleCompra.builder()
                        .compra(compra)
                        .nombreProducto(itemDto.getNombreProducto())
                        .cantidad(itemDto.getCantidad())
                        .precioUnitario(itemDto.getPrecioUnitario())
                        .build();

                BigDecimal subtotal = itemDto.getPrecioUnitario().multiply(new BigDecimal(itemDto.getCantidad()));
                detalle.setSubtotal(subtotal);

                items.add(detalle);
                totalProductos = totalProductos.add(subtotal);
            }
        }
        
        compra.setItems(items);
        compra.setTotalProductos(totalProductos);
        compra.setTotalCompra(totalProductos.add(compra.getCostoTransporte()));

        return compra;
    }

    public CompraResponseDTO toResponseDTO(Compra compra) {
        return CompraResponseDTO.builder()
                .id(compra.getId())
                .proveedorNombre(compra.getProveedorNombre())
                .proveedorContacto(compra.getProveedorContacto())
                .fechaCompra(compra.getFechaCompra())
                .costoTransporte(compra.getCostoTransporte())
                .totalProductos(compra.getTotalProductos())
                .totalCompra(compra.getTotalCompra())
                .gananciaObtenida(compra.getGananciaObtenida())
                .estadoCompra(compra.getEstadoCompra())
                .notas(compra.getNotas())
                .items(compra.getItems() != null ? compra.getItems().stream().map(this::toDetalleDTO).collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }

    public DetalleCompraDTO toDetalleDTO(DetalleCompra detalle) {
        return DetalleCompraDTO.builder()
                .nombreProducto(detalle.getNombreProducto())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .build();
    }
}
