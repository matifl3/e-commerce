package org.example.malaquita.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.malaquita.config.JwtService;
import org.example.malaquita.dto.AuthResponseDto;
import org.example.malaquita.dto.LoginRequestDto;
import org.example.malaquita.dto.RegistroRequestDto;
import org.example.malaquita.dto.UsuarioResponseDto;
import org.example.malaquita.exception.CredencialesInvalidasException;
import org.example.malaquita.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @PostMapping("/registro")
    public ResponseEntity<AuthResponseDto> registrar(@Valid @RequestBody RegistroRequestDto request) {
        UsuarioResponseDto usuario = usuarioService.registrar(request);
        String token = jwtService.generateToken(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AuthResponseDto.builder()
                        .token(token)
                        .usuario(usuario)
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        if (!usuarioService.validarCredenciales(request.getEmail(), request.getPassword())) {
            throw new CredencialesInvalidasException("Email o contraseña incorrectos");
        }
        UsuarioResponseDto usuario = usuarioService.obtenerPerfilPorEmail(request.getEmail());
        String token = jwtService.generateToken(usuario);
        return ResponseEntity.ok(AuthResponseDto.builder()
                .token(token)
                .usuario(usuario)
                .build());
    }
}