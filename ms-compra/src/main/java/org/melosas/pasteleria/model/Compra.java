package org.melosas.pasteleria.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.melosas.pasteleria.enums.EstadoCompra;

@Entity
@Table(name = "compras")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String proveedorNombre;

    private String proveedorContacto;

    @Column(nullable = false)
    private LocalDateTime fechaCompra;

    private BigDecimal costoTransporte;

    private BigDecimal totalProductos;

    private BigDecimal totalCompra;

    private BigDecimal gananciaObtenida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCompra estadoCompra;

    private String notas;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleCompra> items = new ArrayList<>();
}
