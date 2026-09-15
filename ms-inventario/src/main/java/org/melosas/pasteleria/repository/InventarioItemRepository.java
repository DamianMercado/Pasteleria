package org.melosas.pasteleria.repository;

import org.melosas.pasteleria.model.InventarioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventarioItemRepository extends JpaRepository<InventarioItem, Long> {
    List<InventarioItem> findByCodigoPastel(String codigoPastel);
    Optional<InventarioItem> findFirstByCodigoPastel(String codigoPastel);
    Optional<InventarioItem> findByCodigoPastelAndCompraId(String codigoPastel, Long compraId);
    boolean existsByCodigoPastel(String codigoPastel);
    boolean existsByCodigoPastelAndCompraId(String codigoPastel, Long compraId);

    @Query("SELECT COALESCE(SUM(i.stock), 0) FROM InventarioItem i WHERE i.codigoPastel = :codigoPastel")
    Integer sumStockByCodigoPastel(@Param("codigoPastel") String codigoPastel);

    List<InventarioItem> findByCodigoPastelOrderByFechaVencimientoAsc(String codigoPastel);
}
