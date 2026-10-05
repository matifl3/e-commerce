package org.example.malaquita.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversionDto {

    private Long carritosCompletados;
    private Long carritosAbandonados;
    private Integer tasaPorcentaje;
}