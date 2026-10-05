package org.example.malaquita.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDetalleDto {

    private Long id;
    private Long productoVariacionId;
    private String productoNombre;
    private String colorNombre;
    private String talleNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}