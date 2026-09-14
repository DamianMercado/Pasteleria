package org.melosas.pasteleria.repository;

import org.melosas.pasteleria.enums.EstadoCompra;
import org.melosas.pasteleria.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByEstadoCompra(EstadoCompra estado);
    List<Compra> findByProveedorNombreContainingIgnoreCase(String nombre);
}
