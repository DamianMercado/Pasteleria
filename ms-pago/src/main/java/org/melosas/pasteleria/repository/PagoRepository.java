package org.melosas.pasteleria.repository;

import org.melosas.pasteleria.enums.EstadoPago;
import org.melosas.pasteleria.enums.MetodoPago;
import org.melosas.pasteleria.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByEstadoPago(EstadoPago estado);
    List<Pago> findByMetodoPagoAndEstadoPago(MetodoPago metodo, EstadoPago estado);
    List<Pago> findByVentaId(Long ventaId);
}
