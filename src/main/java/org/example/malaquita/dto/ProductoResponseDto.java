package org.example.malaquita.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDto {

    private Long id;
    private CategoriaDto categoria;
    private String nombre;
    private String descripcion;
    private BigDecimal precioBase;
    private Boolean activo;

    @Builder.Default
    private List<ProductoVariacionDto> variaciones = new ArrayList<>();

    @Builder.Default
    private List<ImagenProductoDto> imagenes = new ArrayList<>();
}