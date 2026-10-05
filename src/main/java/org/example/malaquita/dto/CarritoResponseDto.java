package org.example.malaquita.dto;

import lombok.*;
import org.example.malaquita.entity.enums.EstadoCarrito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoResponseDto {

    private Long id;
    private Long usuarioId;
    private String sessionId;
    private EstadoCarrito estado;
    private LocalDateTime fechaModificacion;
    private BigDecimal total;

    @Builder.Default
    private List<CarritoItemResponseDto> items = new ArrayList<>();
}