package org.example.malaquita.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoVariacionDto {

    private Long id;
    private Long productoId;
    private Long colorId;
    private String colorNombre;
    private Long talleId;
    private String talleNombre;
    private Integer stock;
    private String sku;
}