package org.melosas.pasteleria.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "inventario_items", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"codigoPastel", "compraId"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codigoPastel;

    @Column(nullable = false)
    private String nombrePastel;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Integer ventaCosto;

    private Long compraId;

    private LocalDate fechaVencimiento;
}
