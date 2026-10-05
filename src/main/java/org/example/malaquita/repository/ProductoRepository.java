package org.example.malaquita.repository;

import org.example.malaquita.entity.Producto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Override
    @EntityGraph(attributePaths = {"categoria"})
    List<Producto> findAll();

    @EntityGraph(attributePaths = {"categoria"})
    List<Producto> findByActivoTrue();

    long countByCategoriaId(Long categoriaId);
}