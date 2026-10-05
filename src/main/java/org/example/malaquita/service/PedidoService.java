package org.example.malaquita.service;

import lombok.RequiredArgsConstructor;
import org.example.malaquita.dto.*;
import org.example.malaquita.entity.*;
import org.example.malaquita.entity.enums.EstadoPedido;
import org.example.malaquita.exception.ReglaNegocioException;
import org.example.malaquita.exception.ResourceNotFoundException;
import org.example.malaquita.repository.PedidoDetalleRepository;
import org.example.malaquita.repository.PedidoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final int TOP_LIMITE = 10;

    private final PedidoRepository pedidoRepository;
    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final CarritoService carritoService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    @Transactional
    public PedidoResponseDto crearPedido(Long usuarioId, PedidoRequestDto request) {
        Carrito carrito = carritoService.vincularUsuarioSiAnonimo(request.getCarritoId(), usuarioId);
        List<CarritoItem> items = carritoService.listarItems(carrito.getId());

        if (items.isEmpty()) {
            throw new ReglaNegocioException("No se puede crear un pedido con carrito vacío");
        }

        Usuario usuario = usuarioService.obtenerUsuario(usuarioId);

        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .carrito(carrito)
                .total(BigDecimal.ZERO)
                .estado(EstadoPedido.PENDIENTE)
                .direccionEnvio(request.getDireccionEnvio())
                .metodoPago(request.getMetodoPago())
                .build();
        pedido = pedidoRepository.save(pedido);

        BigDecimal total = BigDecimal.ZERO;
        for (CarritoItem item : items) {
            ProductoVariacion variacion = productoService.obtenerVariacion(item.getProductoVariacion().getId());
            productoService.descontarStock(variacion.getId(), item.getCantidad());

            BigDecimal precioUnitario = variacion.getProducto().getPrecioBase();
            PedidoDetalle detalle = PedidoDetalle.builder()
                    .pedido(pedido)
                    .productoVariacion(variacion)
                    .cantidad(item.getCantidad())
                    .precioUnitario(precioUnitario)
                    .build();
            pedidoDetalleRepository.save(detalle);
            total = total.add(precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad())));
        }

        pedido.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        pedidoRepository.save(pedido);

        carritoService.marcarCompletado(carrito.getId());
        carritoService.vaciarCarrito(carrito.getId());

        return toResponseDto(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoResponseDto obtenerPedido(Long id, Long usuarioId, boolean esAdmin) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id " + id));
        if (!esAdmin && (pedido.getUsuario() == null || !pedido.getUsuario().getId().equals(usuarioId))) {
            throw new ReglaNegocioException("No tiene permiso para ver este pedido");
        }
        return toResponseDto(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDto> pedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDto> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PedidoResponseDto actualizarEstado(Long pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id " + pedidoId));
        EstadoPedido actual = pedido.getEstado();
        if (actual == nuevoEstado) {
            throw new ReglaNegocioException("El pedido ya está en estado " + actual);
        }
        if (!transicionValida(actual, nuevoEstado)) {
            throw new ReglaNegocioException("Transición inválida de " + actual + " a " + nuevoEstado);
        }
        pedido.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoPedido.CANCELADO) {
            restituirStock(pedido);
        }
        return toResponseDto(pedidoRepository.save(pedido));
    }

    private static boolean transicionValida(EstadoPedido actual, EstadoPedido nuevo) {
        return switch (actual) {
            case PENDIENTE -> nuevo == EstadoPedido.PAGADO || nuevo == EstadoPedido.CANCELADO;
            case PAGADO -> nuevo == EstadoPedido.ENVIADO || nuevo == EstadoPedido.CANCELADO;
            case ENVIADO -> nuevo == EstadoPedido.ENTREGADO;
            default -> false;
        };
    }

    private void restituirStock(Pedido pedido) {
        pedidoDetalleRepository.findByPedidoId(pedido.getId())
                .forEach(detalle -> productoService.restituirStock(
                        detalle.getProductoVariacion().getId(), detalle.getCantidad()));
    }

    // ---------- Estadisticas ----------

    @Transactional(readOnly = true)
    public TotalVentasDto totalesVentas() {
        BigDecimal total = pedidoRepository.sumTotalNoCancelado();
        return TotalVentasDto.builder()
                .periodo("TOTAL")
                .montoTotal(total != null ? total.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                .cantidadPedidos(pedidoRepository.contarNoCancelados())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TopProductoDto> topProductos() {
        List<TopProductoDto> resultado = new ArrayList<>();
        pedidoDetalleRepository.topProductosPorVentas(PageRequest.of(0, TOP_LIMITE)).forEach(fila -> {
            Producto producto = (Producto) fila[0];
            Long unidades = ((Number) fila[1]).longValue();
            BigDecimal total = (BigDecimal) fila[2];
            resultado.add(TopProductoDto.builder()
                    .productoId(producto.getId())
                    .nombre(producto.getNombre())
                    .unidadesVendidas(unidades)
                    .totalVendido(total != null ? total.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                    .build());
        });
        return resultado;
    }

    // ---------- Mappers ----------

    private PedidoResponseDto toResponseDto(Pedido pedido) {
        List<PedidoDetalleDto> detalles = pedidoDetalleRepository.findByPedidoId(pedido.getId()).stream()
                .map(this::toDetalleDto)
                .collect(Collectors.toList());
        return PedidoResponseDto.builder()
                .id(pedido.getId())
                .usuarioId(pedido.getUsuario().getId())
                .carritoId(pedido.getCarrito() != null ? pedido.getCarrito().getId() : null)
                .fechaPedido(pedido.getFechaPedido())
                .total(pedido.getTotal())
                .estado(pedido.getEstado())
                .direccionEnvio(pedido.getDireccionEnvio())
                .metodoPago(pedido.getMetodoPago())
                .detalles(detalles)
                .build();
    }

    private PedidoDetalleDto toDetalleDto(PedidoDetalle detalle) {
        ProductoVariacion variacion = detalle.getProductoVariacion();
        BigDecimal subtotal = detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
        return PedidoDetalleDto.builder()
                .id(detalle.getId())
                .productoVariacionId(variacion.getId())
                .productoNombre(variacion.getProducto().getNombre())
                .colorNombre(variacion.getColor().getNombre())
                .talleNombre(variacion.getTalle().getNombre())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}