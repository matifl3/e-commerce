package org.example.malaquita.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalleDto {

    private Long id;

    @NotBlank
    private String nombre;

    private Integer orden;
}