package org.melosas.pasteleria.repository;

import org.melosas.pasteleria.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    @Query("SELECT d.codigoPastel, d.nombrePastel, SUM(d.cantidad) as totalVendido FROM DetalleVenta d WHERE d.venta.estadoVenta = 'COMPLETADA' GROUP BY d.codigoPastel, d.nombrePastel ORDER BY totalVendido DESC")
    List<Object[]> findProductosMasVendidos();
}
