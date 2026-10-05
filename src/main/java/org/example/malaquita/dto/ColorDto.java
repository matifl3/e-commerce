package org.example.malaquita.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColorDto {

    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String codigoHex;
}