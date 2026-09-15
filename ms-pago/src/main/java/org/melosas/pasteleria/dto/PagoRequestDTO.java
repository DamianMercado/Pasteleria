package org.melosas.pasteleria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class PagoRequestDTO {
    @NotNull(message = "El ID de la venta es obligatorio")
    private Long ventaId;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String clienteNombre;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal monto;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;

    private EstadoPago estadoPago;

    private LocalDateTime fechaVencimiento;

    private String notas;
}
