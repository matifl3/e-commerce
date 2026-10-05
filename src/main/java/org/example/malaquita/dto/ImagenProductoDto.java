package org.example.malaquita.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenProductoDto {

    private Long id;
    private Long productoId;
    private Long colorId;
    private String urlImagen;
    private Integer orden;
    private Boolean esPrincipal;
}