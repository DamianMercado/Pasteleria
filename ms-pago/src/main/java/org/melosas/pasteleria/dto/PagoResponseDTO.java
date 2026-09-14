package org.melosas.pasteleria.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.melosas.pasteleria.enums.EstadoPago;
import org.melosas.pasteleria.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    private Long id;
    private Long ventaId;
    private String clienteNombre;
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private EstadoPago estadoPago;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaVencimiento;
    private String numeroComprobante;
    private String notas;
}
