package org.example.malaquita.dto;

import lombok.*;
import org.example.malaquita.entity.enums.RolUsuario;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDto {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private RolUsuario rol;
    private LocalDateTime fechaCreacion;
}