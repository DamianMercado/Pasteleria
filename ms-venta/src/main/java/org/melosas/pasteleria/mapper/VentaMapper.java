package org.melosas.pasteleria.mapper;

import org.melosas.pasteleria.dto.DetalleVentaDTO;
import org.melosas.pasteleria.dto.VentaRequestDTO;
import org.melosas.pasteleria.dto.VentaResponseDTO;
import org.melosas.pasteleria.enums.EstadoVenta;
import org.melosas.pasteleria.model.DetalleVenta;
import org.melosas.pasteleria.model.Venta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.List;

@Component
public class VentaMapper {

    public Venta toEntity(VentaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Venta venta = Venta.builder()
                .calle(dto.getCalle())
                .ciudad(dto.getCiudad())
                .clienteNombre(dto.getClienteNombre())
                .notas(dto.getNotas())
                .fechaVenta(LocalDateTime.now())
                .estadoVenta(EstadoVenta.COMPLETADA)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<DetalleVenta> detalles = dto.getItems().stream().map(itemDto -> {
                DetalleVenta detalle = DetalleVenta.builder()
                        .codigoPastel(itemDto.getCodigoPastel())
                        .nombrePastel(itemDto.getNombrePastel())
                        .cantidad(itemDto.getCantidad())
                        .precioUnitario(itemDto.getPrecioUnitario())
                        .vendidoACosto(itemDto.getVendidoACosto() != null ? itemDto.getVendidoACosto() : false)
                        .venta(venta)
                        .build();

                BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
                detalle.setSubtotal(subtotal);
                return detalle;
            }).collect(Collectors.toList());

            venta.setItems(detalles);

            for (DetalleVenta d : detalles) {
                total = total.add(d.getSubtotal());
            }
        }

        venta.setTotalVenta(total);

        return venta;
    }

    public VentaResponseDTO toResponseDTO(Venta entity) {
        if (entity == null) {
            return null;
        }

        List<DetalleVentaDTO> itemsDto = null;
        if (entity.getItems() != null) {
            itemsDto = entity.getItems().stream().map(d -> DetalleVentaDTO.builder()
                    .codigoPastel(d.getCodigoPastel())
                    .nombrePastel(d.getNombrePastel())
                    .cantidad(d.getCantidad())
                    .precioUnitario(d.getPrecioUnitario())
                    .vendidoACosto(d.getVendidoACosto())
                    .build()).collect(Collectors.toList());
        }

        return VentaResponseDTO.builder()
                .id(entity.getId())
                .fechaVenta(entity.getFechaVenta())
                .calle(entity.getCalle())
                .ciudad(entity.getCiudad())
                .clienteNombre(entity.getClienteNombre())
                .totalVenta(entity.getTotalVenta())
                .estadoVenta(entity.getEstadoVenta())
                .notas(entity.getNotas())
                .items(itemsDto)
                .build();
    }
}
