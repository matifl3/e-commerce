package org.example.malaquita.repository;

import org.example.malaquita.entity.Pedido;
import org.example.malaquita.entity.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioId(Long usuarioId);

    List<Pedido> findByEstado(EstadoPedido estado);

    long countByEstado(EstadoPedido estado);

    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.estado <> 'CANCELADO'")
    BigDecimal sumTotalNoCancelado();

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado <> 'CANCELADO'")
    long contarNoCancelados();
}