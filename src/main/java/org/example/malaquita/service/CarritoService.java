package org.example.malaquita.service;

import lombok.RequiredArgsConstructor;
import org.example.malaquita.dto.CarritoItemRequestDto;
import org.example.malaquita.dto.CarritoItemResponseDto;
import org.example.malaquita.dto.CarritoResponseDto;
import org.example.malaquita.dto.ConversionDto;
import org.example.malaquita.entity.Carrito;
import org.example.malaquita.entity.CarritoItem;
import org.example.malaquita.entity.ProductoVariacion;
import org.example.malaquita.entity.enums.EstadoCarrito;
import org.example.malaquita.exception.ReglaNegocioException;
import org.example.malaquita.exception.ResourceNotFoundException;
import org.example.malaquita.repository.CarritoItemRepository;
import org.example.malaquita.repository.CarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;

    @Transactional
    public Carrito obtenerCarritoActivo(Long usuarioId, String sessionId) {
        Optional<Carrito> existente = usuarioId != null
                ? carritoRepository.findByUsuarioId(usuarioId)
                : (sessionId != null ? carritoRepository.findBySessionId(sessionId) : Optional.empty());

        if (existente.isPresent() && existente.get().getEstado() == EstadoCarrito.ACTIVO) {
            return existente.get();
        }

        if (existente.isPresent()) {
            Carrito carrito = existente.get();
            carrito.setEstado(EstadoCarrito.ACTIVO);
            if (usuarioId != null && carrito.getUsuario() == null) {
                carrito.setUsuario(usuarioService.obtenerUsuario(usuarioId));
            }
            marcarModificado(carrito);
            return carrito;
        }

        Carrito carrito = Carrito.builder()
                .usuario(usuarioId != null ? usuarioService.obtenerUsuario(usuarioId) : null)
                .sessionId(sessionId)
                .estado(EstadoCarrito.ACTIVO)
                .build();
        carrito.setFechaModificacion(LocalDateTime.now());
        return carritoRepository.save(carrito);
    }

    @Transactional
    public CarritoResponseDto obtenerCarritoActivoDto(Long usuarioId, String sessionId) {
        return toResponseDto(obtenerCarritoActivo(usuarioId, sessionId));
    }

    @Transactional(readOnly = true)
    public Carrito obtenerCarrito(Long id) {
        return carritoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado con id " + id));
    }

    @Transactional(readOnly = true)
    public void verificarAcceso(Long carritoId, Long usuarioId, String sessionId) {
        Carrito carrito = obtenerCarrito(carritoId);
        boolean esUsuario = usuarioId != null && carrito.getUsuario() != null
                && carrito.getUsuario().getId().equals(usuarioId);
        boolean esSesion = sessionId != null && sessionId.equals(carrito.getSessionId());
        if (!esUsuario && !esSesion) {
            throw new ReglaNegocioException("No tiene acceso a este carrito");
        }
    }

    @Transactional(readOnly = true)
    public List<CarritoItem> listarItems(Long carritoId) {
        return carritoItemRepository.findByCarritoId(carritoId);
    }

    @Transactional
    public CarritoResponseDto agregarItem(Long carritoId, CarritoItemRequestDto request) {
        Carrito carrito = obtenerCarrito(carritoId);
        ProductoVariacion variacion = productoService.obtenerVariacion(request.getProductoVariacionId());

        CarritoItem item = carritoItemRepository
                .findByCarritoId(carritoId).stream()
                .filter(i -> i.getProductoVariacion().getId().equals(request.getProductoVariacionId()))
                .findFirst()
                .orElseGet(() -> CarritoItem.builder()
                        .carrito(carrito)
                        .productoVariacion(variacion)
                        .cantidad(0)
                        .build());

        item.setCantidad(item.getCantidad() + request.getCantidad());
        productoService.validarStock(request.getProductoVariacionId(), item.getCantidad());
        carritoItemRepository.save(item);

        marcarModificado(carrito);
        return toResponseDto(carrito);
    }

    @Transactional
    public CarritoResponseDto actualizarCantidad(Long carritoId, Long itemId, Integer cantidad) {
        Carrito carrito = obtenerCarrito(carritoId);
        CarritoItem item = obtenerItem(itemId);

        if (cantidad <= 0) {
            return quitarItem(carritoId, itemId);
        }

        productoService.validarStock(item.getProductoVariacion().getId(), cantidad);
        item.setCantidad(cantidad);
        carritoItemRepository.save(item);
        marcarModificado(carrito);
        return toResponseDto(carrito);
    }

    @Transactional
    public CarritoResponseDto quitarItem(Long carritoId, Long itemId) {
        Carrito carrito = obtenerCarrito(carritoId);
        CarritoItem item = obtenerItem(itemId);
        carritoItemRepository.delete(item);
        marcarModificado(carrito);
        return toResponseDto(carrito);
    }

    @Transactional
    public void vaciarCarrito(Long carritoId) {
        List<CarritoItem> items = carritoItemRepository.findByCarritoId(carritoId);
        carritoItemRepository.deleteAll(items);
        marcarModificado(obtenerCarrito(carritoId));
    }

    @Transactional
    public void marcarCompletado(Long carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.setEstado(EstadoCarrito.COMPLETADO);
        marcarModificado(carrito);
    }

    @Transactional
    public Carrito vincularUsuarioSiAnonimo(Long carritoId, Long usuarioId) {
        Carrito carrito = obtenerCarrito(carritoId);
        if (carrito.getUsuario() == null) {
            carrito.setUsuario(usuarioService.obtenerUsuario(usuarioId));
        } else if (!carrito.getUsuario().getId().equals(usuarioId)) {
            throw new ReglaNegocioException("No puede crear un pedido con el carrito de otro usuario");
        }
        marcarModificado(carrito);
        return carrito;
    }

    @Transactional
    public void marcarAbandonados(int dias) {
        LocalDateTime corte = LocalDateTime.now().minusDays(dias);
        carritoRepository.findByEstadoAndFechaModificacionBefore(EstadoCarrito.ACTIVO, corte)
                .forEach(carrito -> {
                    carrito.setEstado(EstadoCarrito.ABANDONADO);
                    marcarModificado(carrito);
                });
    }

    @Transactional(readOnly = true)
    public CarritoItem obtenerItem(Long id) {
        return carritoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item del carrito no encontrado con id " + id));
    }

    // ---------- Fecha de modificacion ----------

    @Transactional
    public void marcarModificado(Carrito carrito) {
        carrito.setFechaModificacion(LocalDateTime.now());
        carritoRepository.save(carrito);
    }

    // ---------- Estadisticas ----------

    @Transactional(readOnly = true)
    public long carritosAbandonados() {
        return carritoRepository.countByEstado(EstadoCarrito.ABANDONADO);
    }

    @Transactional(readOnly = true)
    public ConversionDto conversion() {
        long completados = carritoRepository.countByEstado(EstadoCarrito.COMPLETADO);
        long abandonados = carritoRepository.countByEstado(EstadoCarrito.ABANDONADO);
        int tasa = 0;
        if (completados + abandonados > 0) {
            tasa = (int) Math.round(completados * 100.0 / (completados + abandonados));
        }
        return ConversionDto.builder()
                .carritosCompletados(completados)
                .carritosAbandonados(abandonados)
                .tasaPorcentaje(tasa)
                .build();
    }

    // ---------- Mappers ----------

    private CarritoResponseDto toResponseDto(Carrito carrito) {
        List<CarritoItemResponseDto> items = carritoItemRepository.findByCarritoId(carrito.getId()).stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
        BigDecimal total = items.stream()
                .map(i -> i.getSubtotal() != null ? i.getSubtotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return CarritoResponseDto.builder()
                .id(carrito.getId())
                .usuarioId(carrito.getUsuario() != null ? carrito.getUsuario().getId() : null)
                .sessionId(carrito.getSessionId())
                .estado(carrito.getEstado())
                .fechaModificacion(carrito.getFechaModificacion())
                .total(total)
                .items(items)
                .build();
    }

    private CarritoItemResponseDto toItemDto(CarritoItem item) {
        ProductoVariacion variacion = item.getProductoVariacion();
        BigDecimal precioUnitario = variacion.getProducto().getPrecioBase();
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));
        return CarritoItemResponseDto.builder()
                .id(item.getId())
                .productoVariacionId(variacion.getId())
                .productoNombre(variacion.getProducto().getNombre())
                .colorNombre(variacion.getColor().getNombre())
                .talleNombre(variacion.getTalle().getNombre())
                .cantidad(item.getCantidad())
                .precioUnitario(precioUnitario)
                .subtotal(subtotal)
                .build();
    }
}