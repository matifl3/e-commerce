package org.example.malaquita.repository;

import org.example.malaquita.entity.ImagenProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long> {

    List<ImagenProducto> findByProductoId(Long productoId);

    List<ImagenProducto> findByProductoIdAndEsPrincipalTrue(Long productoId);

    List<ImagenProducto> findByProductoIdIn(Collection<Long> productoIds);
}