package org.example.malaquita.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariacionRequestDto {

    @NotNull
    private Long colorId;

    @NotNull
    private Long talleId;

    @NotNull
    @Positive
    private Integer stock;

    @NotBlank
    private String sku;
}