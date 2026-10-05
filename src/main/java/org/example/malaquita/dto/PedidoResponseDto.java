package org.example.malaquita.dto;

import lombok.*;
import org.example.malaquita.entity.enums.EstadoPedido;
import org.example.malaquita.entity.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponseDto {

    private Long id;
    private Long usuarioId;
    private Long carritoId;
    private LocalDateTime fechaPedido;
    private BigDecimal total;
    private EstadoPedido estado;
    private String direccionEnvio;
    private MetodoPago metodoPago;

    @Builder.Default
    private List<PedidoDetalleDto> detalles = new ArrayList<>();
}