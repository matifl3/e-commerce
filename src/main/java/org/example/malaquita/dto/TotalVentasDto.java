package org.example.malaquita.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalVentasDto {

    private String periodo;
    private BigDecimal montoTotal;
    private Long cantidadPedidos;
}