package org.example.malaquita.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoMasVistoDto {

    private Long productoId;
    private String nombre;
    private Long visitas;
}