package org.example.malaquita.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.malaquita.dto.ColorDto;
import org.example.malaquita.dto.ProductoRequestDto;
import org.example.malaquita.dto.ProductoResponseDto;
import org.example.malaquita.dto.ProductoVariacionDto;
import org.example.malaquita.dto.TalleDto;
import org.example.malaquita.dto.VariacionRequestDto;
import org.example.malaquita.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public List<ProductoResponseDto> listar(@RequestParam(defaultValue = "true") boolean activos) {
        return productoService.listarProductos(activos);
    }

    @GetMapping("/{id}")
    public ProductoResponseDto obtener(@PathVariable Long id) {
        return productoService.obtenerProductoDto(id);
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDto> crear(@Valid @RequestBody ProductoRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crearProducto(request));
    }

    @PutMapping("/{id}")
    public ProductoResponseDto actualizar(@PathVariable Long id,
                                          @Valid @RequestBody ProductoRequestDto request) {
        return productoService.actualizarProducto(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        productoService.desactivarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/visitas")
    public ResponseEntity<Void> registrarVisita(@PathVariable Long id,
                                                @RequestHeader(value = "X-Sesion-Id", required = false) String sesionId) {
        productoService.registrarVisita(id, sesionId);
        return ResponseEntity.ok().build();
    }

    // ---------- Colores ----------

    @GetMapping("/colores")
    public List<ColorDto> listarColores() {
        return productoService.listarColores();
    }

    @PostMapping("/colores")
    public ResponseEntity<ColorDto> crearColor(@Valid @RequestBody ColorDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crearColor(request.getNombre(), request.getCodigoHex()));
    }

    // ---------- Talles ----------

    @GetMapping("/talles")
    public List<TalleDto> listarTalles() {
        return productoService.listarTalles();
    }

    @PostMapping("/talles")
    public ResponseEntity<TalleDto> crearTalle(@Valid @RequestBody TalleDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crearTalle(request.getNombre(), request.getOrden()));
    }

    // ---------- Variaciones ----------

    @PostMapping("/{id}/variaciones")
    public ResponseEntity<ProductoVariacionDto> crearVariacion(@PathVariable Long id,
                                                               @Valid @RequestBody VariacionRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crearVariacion(id, request));
    }

    @PutMapping("/variaciones/{variacionId}/stock")
    public ProductoVariacionDto actualizarStock(@PathVariable Long variacionId,
                                                @RequestParam Integer stock) {
        return productoService.actualizarStock(variacionId, stock);
    }
}