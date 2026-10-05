package org.example.malaquita.controller;

import lombok.RequiredArgsConstructor;
import org.example.malaquita.dto.*;
import org.example.malaquita.service.EstadisticaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    @GetMapping("/ventas")
    public TotalVentasDto totalesVentas() {
        return estadisticaService.totalesVentas();
    }

    @GetMapping("/top-productos")
    public List<TopProductoDto> topProductos() {
        return estadisticaService.topProductos();
    }

    @GetMapping("/mas-vistos")
    public List<ProductoMasVistoDto> productoMasVisto() {
        return estadisticaService.productoMasVisto();
    }

    @GetMapping("/stock-bajo")
    public List<StockBajoDto> stockBajo() {
        return estadisticaService.stockBajo();
    }

    @GetMapping("/inventario")
    public List<ProductoResponseDto> inventario() {
        return estadisticaService.inventario();
    }

    @GetMapping("/usuarios-registrados")
    public long usuariosRegistrados() {
        return estadisticaService.usuariosRegistrados();
    }

    @GetMapping("/nuevos-por-mes")
    public List<UsuariosNuevosPorMesDto> nuevosPorMes() {
        return estadisticaService.nuevosPorMes();
    }

    @GetMapping("/carritos-abandonados")
    public long carritosAbandonados() {
        return estadisticaService.carritosAbandonados();
    }

    @GetMapping("/conversion")
    public ConversionDto conversion() {
        return estadisticaService.conversion();
    }
}