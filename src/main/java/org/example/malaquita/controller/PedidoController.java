package org.example.malaquita.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.malaquita.dto.PedidoRequestDto;
import org.example.malaquita.dto.PedidoResponseDto;
import org.example.malaquita.entity.enums.EstadoPedido;
import org.example.malaquita.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponseDto> crear(@Valid @RequestBody PedidoRequestDto request,
                                                   Authentication authentication) {
        Long usuarioId = usuarioIdDe(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.crearPedido(usuarioId, request));
    }

    @GetMapping("/mios")
    public List<PedidoResponseDto> misPedidos(Authentication authentication) {
        Long usuarioId = usuarioIdDe(authentication);
        return pedidoService.pedidosPorUsuario(usuarioId);
    }

    @GetMapping
    public List<PedidoResponseDto> listarTodos() {
        return pedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public PedidoResponseDto obtener(@PathVariable Long id, Authentication authentication) {
        return pedidoService.obtenerPedido(id, usuarioIdDe(authentication), esAdmin(authentication));
    }

    @PutMapping("/{id}/estado")
    public PedidoResponseDto actualizarEstado(@PathVariable Long id,
                                              @RequestParam EstadoPedido estado) {
        return pedidoService.actualizarEstado(id, estado);
    }

    private Long usuarioIdDe(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Number uid = jwt.getClaim("uid");
        return uid.longValue();
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}