package org.melosas.pasteleria.repository;

import org.melosas.pasteleria.model.InventarioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventarioItemRepository extends JpaRepository<InventarioItem, Long> {
    Optional<InventarioItem> findByCodigoPastel(String codigoPastel);
    boolean existsByCodigoPastel(String codigoPastel);
}
