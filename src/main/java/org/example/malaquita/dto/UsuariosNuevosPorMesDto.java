package org.example.malaquita.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuariosNuevosPorMesDto {

    private String mes;
    private Long cantidad;
}