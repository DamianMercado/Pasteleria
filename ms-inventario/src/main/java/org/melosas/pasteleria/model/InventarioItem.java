package org.melosas.pasteleria.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventario_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigoPastel;

    @Column(nullable = false)
    private String nombrePastel;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Integer ventaCosto;

    private Long compraId;
}
