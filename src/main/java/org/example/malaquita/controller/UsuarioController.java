package org.example.malaquita.controller;

import lombok.RequiredArgsConstructor;
import org.example.malaquita.dto.UsuarioResponseDto;
import org.example.malaquita.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public UsuarioResponseDto obtenerPerfilActual(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getSubject();
        return usuarioService.obtenerPerfilPorEmail(email);
    }
}