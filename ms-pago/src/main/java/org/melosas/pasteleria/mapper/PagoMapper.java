package org.melosas.pasteleria.mapper;

import org.melosas.pasteleria.dto.PagoRequestDTO;
import org.melosas.pasteleria.dto.PagoResponseDTO;
import org.melosas.pasteleria.enums.EstadoPago;
import org.melosas.pasteleria.enums.MetodoPago;
import org.melosas.pasteleria.model.Pago;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PagoMapper {

    public Pago toEntity(PagoRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        EstadoPago estado = dto.getMetodoPago() == MetodoPago.FIADO ? EstadoPago.PENDIENTE : EstadoPago.PAGADO;
        String comprobante = "COMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return Pago.builder()
                .ventaId(dto.getVentaId())
                .clienteNombre(dto.getClienteNombre())
                .monto(dto.getMonto())
                .metodoPago(dto.getMetodoPago())
                .estadoPago(estado)
                .fechaPago(LocalDateTime.now())
                .fechaVencimiento(dto.getFechaVencimiento())
                .numeroComprobante(comprobante)
                .notas(dto.getNotas())
                .build();
    }

    public PagoResponseDTO toResponseDTO(Pago entity) {
        if (entity == null) {
            return null;
        }

        return PagoResponseDTO.builder()
                .id(entity.getId())
                .ventaId(entity.getVentaId())
                .clienteNombre(entity.getClienteNombre())
                .monto(entity.getMonto())
                .metodoPago(entity.getMetodoPago())
                .estadoPago(entity.getEstadoPago())
                .fechaPago(entity.getFechaPago())
                .fechaVencimiento(entity.getFechaVencimiento())
                .numeroComprobante(entity.getNumeroComprobante())
                .notas(entity.getNotas())
                .build();
    }
}
