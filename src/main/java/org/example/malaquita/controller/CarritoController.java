package org.example.malaquita.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.malaquita.dto.CarritoItemRequestDto;
import org.example.malaquita.dto.CarritoResponseDto;
import org.example.malaquita.service.CarritoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carritos")
@RequiredArgsConstructor
public class CarritoController {

    private static final String SESION_HEADER = "X-Sesion-Id";

    private final CarritoService carritoService;

    @GetMapping("/activo")
    public CarritoResponseDto obtenerActivo(@RequestHeader(value = SESION_HEADER, required = false) String sesionId,
                                            Authentication authentication) {
        Long usuarioId = usuarioIdDe(authentication);
        return carritoService.obtenerCarritoActivoDto(usuarioId, sesionId);
    }

    @PostMapping("/{carritoId}/items")
    public CarritoResponseDto agregarItem(@PathVariable Long carritoId,
                                          @RequestHeader(value = SESION_HEADER, required = false) String sesionId,
                                          @Valid @RequestBody CarritoItemRequestDto request,
                                          Authentication authentication) {
        carritoService.verificarAcceso(carritoId, usuarioIdDe(authentication), sesionId);
        return carritoService.agregarItem(carritoId, request);
    }

    @PutMapping("/{carritoId}/items/{itemId}")
    public CarritoResponseDto actualizarCantidad(@PathVariable Long carritoId,
                                                 @PathVariable Long itemId,
                                                 @RequestParam Integer cantidad,
                                                 @RequestHeader(value = SESION_HEADER, required = false) String sesionId,
                                                 Authentication authentication) {
        carritoService.verificarAcceso(carritoId, usuarioIdDe(authentication), sesionId);
        return carritoService.actualizarCantidad(carritoId, itemId, cantidad);
    }

    @DeleteMapping("/{carritoId}/items/{itemId}")
    public CarritoResponseDto quitarItem(@PathVariable Long carritoId,
                                         @PathVariable Long itemId,
                                         @RequestHeader(value = SESION_HEADER, required = false) String sesionId,
                                         Authentication authentication) {
        carritoService.verificarAcceso(carritoId, usuarioIdDe(authentication), sesionId);
        return carritoService.quitarItem(carritoId, itemId);
    }

    private Long usuarioIdDe(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            Number uid = jwt.getClaim("uid");
            return uid != null ? uid.longValue() : null;
        }
        return null;
    }
}