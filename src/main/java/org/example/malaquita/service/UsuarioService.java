package org.example.malaquita.service;

import lombok.RequiredArgsConstructor;
import org.example.malaquita.dto.RegistroRequestDto;
import org.example.malaquita.dto.UsuarioResponseDto;
import org.example.malaquita.dto.UsuariosNuevosPorMesDto;
import org.example.malaquita.entity.Usuario;
import org.example.malaquita.entity.enums.RolUsuario;
import org.example.malaquita.exception.DuplicadoException;
import org.example.malaquita.exception.ResourceNotFoundException;
import org.example.malaquita.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDto registrar(RegistroRequestDto request) {
        String email = request.getEmail().toLowerCase().trim();
        if (usuarioRepository.existsByEmail(email)) {
            throw new DuplicadoException("Ya existe un usuario con el email " + email);
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre().trim())
                .apellido(request.getApellido().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .rol(RolUsuario.USUARIO)
                .build();
        usuario.setFechaCreacion(LocalDateTime.now());
        return toResponseDto(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }

    @Transactional(readOnly = true)
    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email " + email));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDto obtenerPerfil(Long id) {
        return toResponseDto(obtenerUsuario(id));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDto obtenerPerfilPorEmail(String email) {
        return toResponseDto(obtenerPorEmail(email));
    }

    @Transactional(readOnly = true)
    public boolean validarCredenciales(String email, String password) {
        return usuarioRepository.findByEmail(email.trim().toLowerCase())
                .map(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public long usuariosRegistrados() {
        return usuarioRepository.count();
    }

    @Transactional(readOnly = true)
    public List<UsuariosNuevosPorMesDto> nuevosPorMes() {
        List<UsuariosNuevosPorMesDto> resultado = new ArrayList<>();
        usuarioRepository.countNuevosPorMes().forEach(fila -> {
            int anio = ((Number) fila[0]).intValue();
            int mes = ((Number) fila[1]).intValue();
            resultado.add(UsuariosNuevosPorMesDto.builder()
                    .mes(String.format("%d-%02d", anio, mes))
                    .cantidad(((Number) fila[2]).longValue())
                    .build());
        });
        return resultado;
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDto toResponseDto(Usuario usuario) {
        return UsuarioResponseDto.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .rol(usuario.getRol())
                .fechaCreacion(usuario.getFechaCreacion())
                .build();
    }
}