package org.example.malaquita.repository;

import org.example.malaquita.entity.Carrito;
import org.example.malaquita.entity.enums.EstadoCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findBySessionId(String sessionId);

    Optional<Carrito> findByUsuarioId(Long usuarioId);

    List<Carrito> findByEstado(EstadoCarrito estado);

    long countByEstado(EstadoCarrito estado);

    List<Carrito> findByEstadoAndFechaModificacionBefore(EstadoCarrito estado, LocalDateTime fecha);
}