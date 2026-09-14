package org.melosas.pasteleria.repository;

import org.melosas.pasteleria.enums.EstadoVenta;
import org.melosas.pasteleria.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByEstadoVenta(EstadoVenta estado);

    List<Venta> findByFechaVentaBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(v.totalVenta) FROM Venta v WHERE v.fechaVenta BETWEEN :start AND :end AND v.estadoVenta = 'COMPLETADA'")
    BigDecimal sumTotalVentaByFechaBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fechaVenta BETWEEN :start AND :end AND v.estadoVenta = 'COMPLETADA'")
    Long countVentasByFechaBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
