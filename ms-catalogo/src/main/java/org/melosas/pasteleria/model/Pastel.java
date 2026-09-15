package org.melosas.pasteleria.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pasteles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pastel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigoPastel;

    @Column(nullable = true)
    private String nombrePastel;

    private String categoria;

    @Column(nullable = false)
    private BigDecimal precioPastel;

    @Column(nullable = false)
    private BigDecimal precioVenta;

    private BigDecimal precioCosto;

    private Integer pesoPastel;

    private LocalDate fechaVencimiento;

    private Long compraId;
}
