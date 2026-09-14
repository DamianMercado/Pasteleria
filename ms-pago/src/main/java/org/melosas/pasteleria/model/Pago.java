package org.melosas.pasteleria.model;

import jakarta.persistence.*;
import lombok.*;
import org.melosas.pasteleria.enums.EstadoPago;
import org.melosas.pasteleria.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ventaId;

    @Column(nullable = false)
    private String clienteNombre;

    @Column(nullable = false)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estadoPago;

    @Column(nullable = false)
    private LocalDateTime fechaPago;

    private LocalDateTime fechaVencimiento;

    private String numeroComprobante;

    private String notas;
}
