package org.example.malaquita.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoItemRequestDto {

    @NotNull
    private Long productoVariacionId;

    @NotNull
    @Positive
    private Integer cantidad;
}