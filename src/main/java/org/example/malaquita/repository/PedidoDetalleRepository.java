package org.example.malaquita.repository;

import org.example.malaquita.entity.PedidoDetalle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {

    List<PedidoDetalle> findByPedidoId(Long pedidoId);

    @Query("SELECT pd.productoVariacion.producto AS producto, "
            + "SUM(pd.cantidad) AS unidades, "
            + "SUM(pd.precioUnitario * pd.cantidad) AS total "
            + "FROM PedidoDetalle pd JOIN pd.pedido p WHERE p.estado <> 'CANCELADO' "
            + "GROUP BY pd.productoVariacion.producto ORDER BY SUM(pd.cantidad) DESC")
    List<Object[]> topProductosPorVentas(Pageable pageable);
}