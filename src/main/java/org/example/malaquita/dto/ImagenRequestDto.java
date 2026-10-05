package org.example.malaquita.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenRequestDto {

    private Long colorId;

    @NotBlank
    private String urlImagen;

    private Integer orden;

    private Boolean esPrincipal;
}