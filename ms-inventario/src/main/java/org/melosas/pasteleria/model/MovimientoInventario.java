package org.melosas.pasteleria.model;

import jakarta.persistence.*;
import lombok.*;
import org.melosas.pasteleria.enums.TipoMovimiento;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codigoPastel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimiento tipoMovimiento;

    @Column(nullable = false)
    private Integer cantidad;

    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fechaMovimiento;
}
