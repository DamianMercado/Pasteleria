package org.melosas.pasteleria.mapper;

import org.melosas.pasteleria.dto.PastelRequestDTO;
import org.melosas.pasteleria.dto.PastelResponseDTO;
import org.melosas.pasteleria.model.Pastel;
import org.springframework.stereotype.Component;

@Component
public class PastelMapper {

    public Pastel toEntity(PastelRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Pastel.builder()
                .codigoPastel(dto.getCodigoPastel())
                .nombrePastel(dto.getNombrePastel())
                .categoria(dto.getCategoria())
                .precioPastel(dto.getPrecioPastel())
                .precioVenta(dto.getPrecioVenta())
                .precioCosto(dto.getPrecioPastel())
                .pesoPastel(dto.getPesoPastel())
                .diasVencimiento(dto.getDiasVencimiento())
                .compraId(dto.getCompraId())
                .build();
    }

    public PastelResponseDTO toResponseDTO(Pastel entity) {
        if (entity == null) {
            return null;
        }
        return PastelResponseDTO.builder()
                .id(entity.getId())
                .codigoPastel(entity.getCodigoPastel())
                .nombrePastel(entity.getNombrePastel())
                .categoria(entity.getCategoria())
                .precioPastel(entity.getPrecioPastel())
                .precioVenta(entity.getPrecioVenta())
                .precioCosto(entity.getPrecioCosto())
                .pesoPastel(entity.getPesoPastel())
                .diasVencimiento(entity.getDiasVencimiento())
                .compraId(entity.getCompraId())
                .stock(null)
                .estadoStock("N/A")
                .build();
    }

    public void updateEntityFromDTO(PastelRequestDTO dto, Pastel entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setCodigoPastel(dto.getCodigoPastel());
        entity.setNombrePastel(dto.getNombrePastel());
        entity.setCategoria(dto.getCategoria());
        entity.setPrecioPastel(dto.getPrecioPastel());
        entity.setPrecioVenta(dto.getPrecioVenta());
        entity.setPrecioCosto(dto.getPrecioPastel());
        entity.setPesoPastel(dto.getPesoPastel());
        entity.setDiasVencimiento(dto.getDiasVencimiento());
        entity.setCompraId(dto.getCompraId());
    }
}
