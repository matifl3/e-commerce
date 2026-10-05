package org.example.malaquita.service;

import lombok.RequiredArgsConstructor;
import org.example.malaquita.dto.CategoriaDto;
import org.example.malaquita.entity.Categoria;
import org.example.malaquita.exception.ReglaNegocioException;
import org.example.malaquita.exception.ResourceNotFoundException;
import org.example.malaquita.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoService productoService;

    @Transactional
    public CategoriaDto crear(CategoriaDto dto) {
        Categoria categoria = Categoria.builder()
                .nombre(dto.getNombre().trim())
                .build();
        return toDto(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaDto> listar() {
        return categoriaRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaDto obtener(Long id) {
        return toDto(buscar(id));
    }

    @Transactional
    public CategoriaDto actualizar(Long id, CategoriaDto dto) {
        Categoria categoria = buscar(id);
        categoria.setNombre(dto.getNombre().trim());
        return toDto(categoriaRepository.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscar(id);
        if (productoService.contarProductosPorCategoria(id) > 0) {
            throw new ReglaNegocioException("No se puede eliminar la categoría porque tiene productos asociados");
        }
        categoriaRepository.delete(categoria);
    }

    private Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id " + id));
    }

    private CategoriaDto toDto(Categoria categoria) {
        return CategoriaDto.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .build();
    }
}