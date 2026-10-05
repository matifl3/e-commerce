package org.example.malaquita.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequestDto {

    @NotNull
    private Long categoriaId;

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotNull
    @Positive
    private BigDecimal precioBase;

    private Boolean activo;

    @Valid
    @Builder.Default
    private List<VariacionRequestDto> variaciones = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<ImagenRequestDto> imagenes = new ArrayList<>();
}