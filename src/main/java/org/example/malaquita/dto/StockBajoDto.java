package org.example.malaquita.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockBajoDto {

    private Long productoId;
    private String nombre;
    private Long variacionId;
    private String sku;
    private String colorNombre;
    private String talleNombre;
    private Integer stock;
    private Integer umbral;
}