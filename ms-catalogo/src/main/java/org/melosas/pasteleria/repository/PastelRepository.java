package org.melosas.pasteleria.repository;

import org.melosas.pasteleria.model.Pastel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PastelRepository extends JpaRepository<Pastel, Long> {
    Optional<Pastel> findByCodigoPastel(String codigoPastel);
    List<Pastel> findByCategoria(String categoria);
    List<Pastel> findByCompraId(Long compraId);
    boolean existsByCodigoPastel(String codigoPastel);
}
