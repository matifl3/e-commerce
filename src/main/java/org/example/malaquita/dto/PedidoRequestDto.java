package org.example.malaquita.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.malaquita.entity.enums.MetodoPago;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequestDto {

    @NotNull
    private Long carritoId;

    @NotBlank
    private String direccionEnvio;

    @NotNull
    private MetodoPago metodoPago;
}