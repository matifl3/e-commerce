package org.example.malaquita.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopProductoDto {

    private Long productoId;
    private String nombre;
    private Long unidadesVendidas;
    private BigDecimal totalVendido;
}