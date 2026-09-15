package org.melosas.pasteleria.service.impl;

import lombok.RequiredArgsConstructor;
import org.melosas.pasteleria.dto.PagoRequestDTO;
import org.melosas.pasteleria.dto.PagoResponseDTO;
import org.melosas.pasteleria.enums.EstadoPago;
import org.melosas.pasteleria.enums.MetodoPago;
import org.melosas.pasteleria.exception.BusinessException;
import org.melosas.pasteleria.exception.ResourceNotFoundException;
import org.melosas.pasteleria.mapper.PagoMapper;
import org.melosas.pasteleria.model.Pago;
import org.melosas.pasteleria.repository.PagoRepository;
import org.melosas.pasteleria.service.PagoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final PagoMapper pagoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarTodos() {
        return pagoRepository.findAll().stream()
                .map(pagoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPorId(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));
        return pagoMapper.toResponseDTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> obtenerPorVentaId(Long ventaId) {
        return pagoRepository.findByVentaId(ventaId).stream()
                .map(pagoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listarFiadosPendientes() {
        return pagoRepository.findByMetodoPagoAndEstadoPago(MetodoPago.FIADO, EstadoPago.PENDIENTE).stream()
                .map(pagoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PagoResponseDTO procesarPago(PagoRequestDTO dto) {
        if (dto.getMetodoPago() == MetodoPago.FIADO && dto.getFechaVencimiento() == null) {
            throw new BusinessException("La fecha de vencimiento es obligatoria para pagos fiados");
        }

        Pago pago = pagoMapper.toEntity(dto);
        pago = pagoRepository.save(pago);
        return pagoMapper.toResponseDTO(pago);
    }

    @Override
    @Transactional
    public PagoResponseDTO marcarComoPagado(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));

        if (pago.getEstadoPago() != EstadoPago.PENDIENTE) {
            throw new BusinessException("El pago no está pendiente");
        }

        pago.setEstadoPago(EstadoPago.PAGADO);
        pago = pagoRepository.save(pago);
        return pagoMapper.toResponseDTO(pago);
    }

    @Override
    @Transactional
    public PagoResponseDTO actualizarPago(Long id, PagoRequestDTO dto) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));

        if (dto.getMetodoPago() == MetodoPago.FIADO && dto.getFechaVencimiento() == null) {
            throw new BusinessException("La fecha de vencimiento es obligatoria para pagos fiados");
        }

        if (dto.getClienteNombre() != null && !dto.getClienteNombre().trim().isEmpty()) {
            pago.setClienteNombre(dto.getClienteNombre().trim());
        }
        if (dto.getMonto() != null) {
            pago.setMonto(dto.getMonto());
        }
        if (dto.getMetodoPago() != null) {
            pago.setMetodoPago(dto.getMetodoPago());
        }
        if (dto.getEstadoPago() != null) {
            pago.setEstadoPago(dto.getEstadoPago());
        } else if (dto.getMetodoPago() != null) {
            if (dto.getMetodoPago() == MetodoPago.FIADO && pago.getEstadoPago() == EstadoPago.PAGADO) {
                pago.setEstadoPago(EstadoPago.PENDIENTE);
            } else if (dto.getMetodoPago() != MetodoPago.FIADO && pago.getEstadoPago() == EstadoPago.PENDIENTE) {
                pago.setEstadoPago(EstadoPago.PAGADO);
            }
        }
        pago.setFechaVencimiento(dto.getFechaVencimiento());
        if (dto.getNotas() != null) {
            pago.setNotas(dto.getNotas());
        }

        pago = pagoRepository.save(pago);
        return pagoMapper.toResponseDTO(pago);
    }

    @Override
    @Transactional
    public void anularPago(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));

        pago.setEstadoPago(EstadoPago.ANULADO);
        pagoRepository.save(pago);
    }
}
