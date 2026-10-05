package org.example.malaquita.service;

import lombok.RequiredArgsConstructor;
import org.example.malaquita.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadisticaService {

    private final PedidoService pedidoService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;
    private final CarritoService carritoService;

    @Transactional(readOnly = true)
    public TotalVentasDto totalesVentas() {
        return pedidoService.totalesVentas();
    }

    @Transactional(readOnly = true)
    public List<TopProductoDto> topProductos() {
        return pedidoService.topProductos();
    }

    @Transactional(readOnly = true)
    public List<ProductoMasVistoDto> productoMasVisto() {
        return productoService.productoMasVisto();
    }

    @Transactional(readOnly = true)
    public List<StockBajoDto> stockBajo() {
        return productoService.stockBajo();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDto> inventario() {
        return productoService.inventario();
    }

    @Transactional(readOnly = true)
    public long usuariosRegistrados() {
        return usuarioService.usuariosRegistrados();
    }

    @Transactional(readOnly = true)
    public List<UsuariosNuevosPorMesDto> nuevosPorMes() {
        return usuarioService.nuevosPorMes();
    }

    @Transactional(readOnly = true)
    public long carritosAbandonados() {
        return carritoService.carritosAbandonados();
    }

    @Transactional(readOnly = true)
    public ConversionDto conversion() {
        return carritoService.conversion();
    }
}