package org.melosas.pasteleria.repository;

import org.melosas.pasteleria.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findByCodigoPastelOrderByFechaMovimientoDesc(String codigoPastel);
}
