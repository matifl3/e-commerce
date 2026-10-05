package org.example.malaquita.repository;

import org.example.malaquita.entity.ProductoVariacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoVariacionRepository extends JpaRepository<ProductoVariacion, Long> {

    List<ProductoVariacion> findByProductoId(Long productoId);

    Optional<ProductoVariacion> findBySku(String sku);

    List<ProductoVariacion> findByStockLessThan(Integer stock);

    List<ProductoVariacion> findByProductoIdIn(Collection<Long> productoIds);
}