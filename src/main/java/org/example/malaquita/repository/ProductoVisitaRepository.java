package org.example.malaquita.repository;

import org.example.malaquita.entity.ProductoVisita;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoVisitaRepository extends JpaRepository<ProductoVisita, Long> {

    @Query("SELECT pv.producto AS producto, COUNT(pv) AS visitas "
            + "FROM ProductoVisita pv GROUP BY pv.producto ORDER BY COUNT(pv) DESC")
    List<Object[]> contarVisitasPorProducto(Pageable pageable);
}