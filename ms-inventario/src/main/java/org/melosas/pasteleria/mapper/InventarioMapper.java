package org.melosas.pasteleria.mapper;

import org.melosas.pasteleria.dto.InventarioItemRequestDTO;
import org.melosas.pasteleria.dto.InventarioItemResponseDTO;
import org.melosas.pasteleria.model.InventarioItem;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class InventarioMapper {
    public InventarioItem toEntity(InventarioItemRequestDTO dto) {
        if (dto == null) return null;
        InventarioItem item = new InventarioItem();
        item.setCodigoPastel(dto.getCodigoPastel());
        item.setNombrePastel(dto.getNombrePastel());
        item.setStock(dto.getStock());
        item.setCompraId(dto.getCompraId());
        item.setFechaVencimiento(dto.getFechaVencimiento());
        item.setVentaCosto(0);
        return item;
    }

    public InventarioItemResponseDTO toResponseDTO(InventarioItem entity) {
        if (entity == null) return null;
        InventarioItemResponseDTO dto = new InventarioItemResponseDTO();
        dto.setId(entity.getId());
        dto.setCodigoPastel(entity.getCodigoPastel());
        dto.setNombrePastel(entity.getNombrePastel());
        dto.setStock(entity.getStock());
        dto.setVentaCosto(entity.getVentaCosto());
        dto.setCompraId(entity.getCompraId());
        dto.setAlertaStockBajo(entity.getStock() <= 5);
        dto.setFechaVencimiento(entity.getFechaVencimiento());

        if (entity.getFechaVencimiento() != null) {
            LocalDate hoy = LocalDate.now();
            dto.setVencido(entity.getFechaVencimiento().isBefore(hoy));
            dto.setDiasParaVencer(ChronoUnit.DAYS.between(hoy, entity.getFechaVencimiento()));
        } else {
            dto.setVencido(false);
            dto.setDiasParaVencer(null);
        }
        return dto;
    }
}
