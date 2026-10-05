package org.example.malaquita.service;

import lombok.RequiredArgsConstructor;
import org.example.malaquita.dto.*;
import org.example.malaquita.entity.*;
import org.example.malaquita.exception.DuplicadoException;
import org.example.malaquita.exception.ResourceNotFoundException;
import org.example.malaquita.exception.StockInsuficienteException;
import org.example.malaquita.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private static final int UMBRAL_STOCK_BAJO = 5;
    private static final int TOP_LIMITE = 10;

    private final ProductoRepository productoRepository;
    private final ProductoVariacionRepository productoVariacionRepository;
    private final ImagenProductoRepository imagenProductoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ColorRepository colorRepository;
    private final TalleRepository talleRepository;
    private final ProductoVisitaRepository productoVisitaRepository;

    // ---------- Productos ----------

    @Transactional
    public ProductoResponseDto crearProducto(ProductoRequestDto request) {
        Categoria categoria = obtenerCategoria(request.getCategoriaId());

        Producto producto = productoRepository.save(Producto.builder()
                .categoria(categoria)
                .nombre(request.getNombre().trim())
                .descripcion(request.getDescripcion())
                .precioBase(request.getPrecioBase())
                .activo(request.getActivo() != null ? request.getActivo() : Boolean.TRUE)
                .build());

        if (request.getVariaciones() != null) {
            request.getVariaciones().forEach(v -> crearVariacion(producto, v));
        }
        if (request.getImagenes() != null) {
            request.getImagenes().forEach(i -> crearImagen(producto, i));
        }
        return toResponseDto(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDto> listarProductos(boolean soloActivos) {
        List<Producto> productos = soloActivos
                ? productoRepository.findByActivoTrue()
                : productoRepository.findAll();
        return toResponseDtos(productos);
    }

    @Transactional(readOnly = true)
    public ProductoResponseDto obtenerProductoDto(Long id) {
        return toResponseDto(obtenerProducto(id));
    }

    @Transactional
    public ProductoResponseDto actualizarProducto(Long id, ProductoRequestDto request) {
        Producto producto = obtenerProducto(id);
        Categoria categoria = obtenerCategoria(request.getCategoriaId());
        producto.setCategoria(categoria);
        producto.setNombre(request.getNombre().trim());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecioBase(request.getPrecioBase());
        producto.setActivo(request.getActivo() != null ? request.getActivo() : producto.getActivo());
        productoRepository.save(producto);

        if (request.getVariaciones() != null) {
            sincronizarVariaciones(producto, request.getVariaciones());
        }
        if (request.getImagenes() != null) {
            reemplazarImagenes(producto, request.getImagenes());
        }
        return toResponseDto(producto);
    }

    @Transactional
    public void desactivarProducto(Long id) {
        Producto producto = obtenerProducto(id);
        producto.setActivo(Boolean.FALSE);
        productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
    }

    @Transactional(readOnly = true)
    public long contarProductosPorCategoria(Long categoriaId) {
        return productoRepository.countByCategoriaId(categoriaId);
    }

    private Categoria obtenerCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id " + id));
    }

    // ---------- Variaciones ----------

    @Transactional
    public ProductoVariacionDto crearVariacion(Long productoId, VariacionRequestDto request) {
        return crearVariacion(obtenerProducto(productoId), request);
    }

    @Transactional
    protected ProductoVariacionDto crearVariacion(Producto producto, VariacionRequestDto request) {
        Color color = obtenerColor(request.getColorId());
        Talle talle = obtenerTalle(request.getTalleId());

        if (productoVariacionRepository.findBySku(request.getSku().trim().toUpperCase()).isPresent()) {
            throw new DuplicadoException("Ya existe una variación con el SKU " + request.getSku());
        }

        ProductoVariacion variacion = ProductoVariacion.builder()
                .producto(producto)
                .color(color)
                .talle(talle)
                .stock(request.getStock())
                .sku(request.getSku().trim().toUpperCase())
                .build();
        return toVariacionDto(productoVariacionRepository.save(variacion));
    }

    @Transactional
    public ProductoVariacionDto actualizarStock(Long variacionId, Integer stock) {
        ProductoVariacion variacion = obtenerVariacion(variacionId);
        variacion.setStock(stock);
        return toVariacionDto(productoVariacionRepository.save(variacion));
    }

    @Transactional(readOnly = true)
    public void validarStock(Long variacionId, Integer cantidad) {
        ProductoVariacion variacion = obtenerVariacion(variacionId);
        if (variacion.getStock() < cantidad) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para la variación " + variacion.getSku()
                            + " (disponible: " + variacion.getStock() + ")");
        }
    }

    @Transactional
    public void descontarStock(Long variacionId, Integer cantidad) {
        ProductoVariacion variacion = obtenerVariacion(variacionId);
        if (variacion.getStock() < cantidad) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para la variación " + variacion.getSku()
                            + " (disponible: " + variacion.getStock() + ")");
        }
        variacion.setStock(variacion.getStock() - cantidad);
        productoVariacionRepository.save(variacion);
    }

    @Transactional
    public void restituirStock(Long variacionId, Integer cantidad) {
        ProductoVariacion variacion = obtenerVariacion(variacionId);
        variacion.setStock(variacion.getStock() + cantidad);
        productoVariacionRepository.save(variacion);
    }

    @Transactional(readOnly = true)
    public ProductoVariacion obtenerVariacion(Long id) {
        return productoVariacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variación no encontrada con id " + id));
    }

    // ---------- Imagenes ----------

    @Transactional
    protected ImagenProductoDto crearImagen(Producto producto, ImagenRequestDto request) {
        Color color = request.getColorId() != null ? obtenerColor(request.getColorId()) : null;
        ImagenProducto imagen = ImagenProducto.builder()
                .producto(producto)
                .color(color)
                .urlImagen(request.getUrlImagen())
                .orden(request.getOrden())
                .esPrincipal(request.getEsPrincipal() != null ? request.getEsPrincipal() : Boolean.FALSE)
                .build();
        return toImagenDto(imagenProductoRepository.save(imagen));
    }

    // ---------- Colores ----------

    @Transactional
    public ColorDto crearColor(String nombre, String codigoHex) {
        String nombreNormalizado = nombre.trim().toUpperCase(Locale.ROOT);
        if (colorRepository.findByNombreIgnoreCase(nombreNormalizado).isPresent()) {
            throw new DuplicadoException("Ya existe un color con el nombre " + nombre);
        }
        Color color = Color.builder()
                .nombre(nombreNormalizado)
                .codigoHex(codigoHex.trim().toUpperCase(Locale.ROOT))
                .build();
        return toColorDto(colorRepository.save(color));
    }

    @Transactional(readOnly = true)
    public List<ColorDto> listarColores() {
        return colorRepository.findAll().stream()
                .map(this::toColorDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Color obtenerColor(Long id) {
        return colorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Color no encontrado con id " + id));
    }

    // ---------- Talles ----------

    @Transactional
    public TalleDto crearTalle(String nombre, Integer orden) {
        String nombreNormalizado = nombre.trim().toUpperCase(Locale.ROOT);
        if (talleRepository.findByNombreIgnoreCase(nombreNormalizado).isPresent()) {
            throw new DuplicadoException("Ya existe un talle con el nombre " + nombre);
        }
        Talle talle = Talle.builder()
                .nombre(nombreNormalizado)
                .orden(orden)
                .build();
        return toTalleDto(talleRepository.save(talle));
    }

    @Transactional(readOnly = true)
    public List<TalleDto> listarTalles() {
        return talleRepository.findAll().stream()
                .map(this::toTalleDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Talle obtenerTalle(Long id) {
        return talleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Talle no encontrado con id " + id));
    }

    // ---------- Visitas (producto mas visto) ----------

    @Transactional
    public void registrarVisita(Long productoId, String sesionId) {
        ProductoVisita visita = ProductoVisita.builder()
                .producto(obtenerProducto(productoId))
                .sesionId(sesionId)
                .build();
        productoVisitaRepository.save(visita);
    }

    @Transactional(readOnly = true)
    public List<ProductoMasVistoDto> productoMasVisto() {
        List<ProductoMasVistoDto> resultado = new ArrayList<>();
        productoVisitaRepository.contarVisitasPorProducto(PageRequest.of(0, TOP_LIMITE)).forEach(fila -> {
            Producto producto = (Producto) fila[0];
            resultado.add(ProductoMasVistoDto.builder()
                    .productoId(producto.getId())
                    .nombre(producto.getNombre())
                    .visitas(((Number) fila[1]).longValue())
                    .build());
        });
        return resultado;
    }

    // ---------- Estadisticas de producto ----------

    @Transactional(readOnly = true)
    public List<StockBajoDto> stockBajo() {
        List<StockBajoDto> resultado = new ArrayList<>();
        productoVariacionRepository.findByStockLessThan(UMBRAL_STOCK_BAJO).forEach(variacion -> {
            resultado.add(StockBajoDto.builder()
                    .productoId(variacion.getProducto().getId())
                    .nombre(variacion.getProducto().getNombre())
                    .variacionId(variacion.getId())
                    .sku(variacion.getSku())
                    .colorNombre(variacion.getColor().getNombre())
                    .talleNombre(variacion.getTalle().getNombre())
                    .stock(variacion.getStock())
                    .umbral(UMBRAL_STOCK_BAJO)
                    .build());
        });
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDto> inventario() {
        return listarProductos(false);
    }

    // ---------- Mappers ----------

    private List<ProductoResponseDto> toResponseDtos(List<Producto> productos) {
        if (productos.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> ids = productos.stream().map(Producto::getId).collect(Collectors.toList());
        Map<Long, List<ProductoVariacion>> variacionesPorProducto = productoVariacionRepository
                .findByProductoIdIn(ids).stream()
                .collect(Collectors.groupingBy(v -> v.getProducto().getId()));
        Map<Long, List<ImagenProducto>> imagenesPorProducto = imagenProductoRepository
                .findByProductoIdIn(ids).stream()
                .collect(Collectors.groupingBy(i -> i.getProducto().getId()));
        return productos.stream()
                .map(p -> toResponseDto(p,
                        variacionesPorProducto.getOrDefault(p.getId(), new ArrayList<>()),
                        imagenesPorProducto.getOrDefault(p.getId(), new ArrayList<>())))
                .collect(Collectors.toList());
    }

    private ProductoResponseDto toResponseDto(Producto producto) {
        return toResponseDto(producto,
                productoVariacionRepository.findByProductoId(producto.getId()),
                imagenProductoRepository.findByProductoId(producto.getId()));
    }

    private ProductoResponseDto toResponseDto(Producto producto,
                                              List<ProductoVariacion> variaciones,
                                              List<ImagenProducto> imagenes) {
        return ProductoResponseDto.builder()
                .id(producto.getId())
                .categoria(toCategoriaDto(producto.getCategoria()))
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precioBase(producto.getPrecioBase())
                .activo(producto.getActivo())
                .variaciones(variaciones.stream().map(this::toVariacionDto).collect(Collectors.toList()))
                .imagenes(imagenes.stream().map(this::toImagenDto).collect(Collectors.toList()))
                .build();
    }

    private ProductoVariacionDto toVariacionDto(ProductoVariacion variacion) {
        return ProductoVariacionDto.builder()
                .id(variacion.getId())
                .productoId(variacion.getProducto().getId())
                .colorId(variacion.getColor().getId())
                .colorNombre(variacion.getColor().getNombre())
                .talleId(variacion.getTalle().getId())
                .talleNombre(variacion.getTalle().getNombre())
                .stock(variacion.getStock())
                .sku(variacion.getSku())
                .build();
    }

    private ImagenProductoDto toImagenDto(ImagenProducto imagen) {
        return ImagenProductoDto.builder()
                .id(imagen.getId())
                .productoId(imagen.getProducto().getId())
                .colorId(imagen.getColor() != null ? imagen.getColor().getId() : null)
                .urlImagen(imagen.getUrlImagen())
                .orden(imagen.getOrden())
                .esPrincipal(imagen.getEsPrincipal())
                .build();
    }

    private CategoriaDto toCategoriaDto(Categoria categoria) {
        return CategoriaDto.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .build();
    }

    private ColorDto toColorDto(Color color) {
        return ColorDto.builder()
                .id(color.getId())
                .nombre(color.getNombre())
                .codigoHex(color.getCodigoHex())
                .build();
    }

    private TalleDto toTalleDto(Talle talle) {
        return TalleDto.builder()
                .id(talle.getId())
                .nombre(talle.getNombre())
                .orden(talle.getOrden())
                .build();
    }

    private void sincronizarVariaciones(Producto producto, List<VariacionRequestDto> variaciones) {
        List<ProductoVariacion> existentes = productoVariacionRepository.findByProductoId(producto.getId());
        Map<String, ProductoVariacion> porSku = existentes.stream()
                .collect(Collectors.toMap(v -> v.getSku().toUpperCase(Locale.ROOT), Function.identity()));
        for (VariacionRequestDto v : variaciones) {
            String sku = v.getSku().trim().toUpperCase(Locale.ROOT);
            ProductoVariacion variacion = porSku.get(sku);
            if (variacion != null) {
                variacion.setColor(obtenerColor(v.getColorId()));
                variacion.setTalle(obtenerTalle(v.getTalleId()));
                variacion.setStock(v.getStock());
                productoVariacionRepository.save(variacion);
            } else {
                if (productoVariacionRepository.findBySku(sku).isPresent()) {
                    throw new DuplicadoException("Ya existe una variación con el SKU " + sku);
                }
                productoVariacionRepository.save(ProductoVariacion.builder()
                        .producto(producto)
                        .color(obtenerColor(v.getColorId()))
                        .talle(obtenerTalle(v.getTalleId()))
                        .stock(v.getStock())
                        .sku(sku)
                        .build());
            }
        }
    }

    private void reemplazarImagenes(Producto producto, List<ImagenRequestDto> imagenes) {
        imagenProductoRepository.deleteAll(imagenProductoRepository.findByProductoId(producto.getId()));
        imagenes.forEach(i -> crearImagen(producto, i));
    }
}