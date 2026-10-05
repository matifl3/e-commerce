package org.example.malaquita.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.malaquita.dto.ImagenRequestDto;
import org.example.malaquita.dto.ProductoRequestDto;
import org.example.malaquita.dto.VariacionRequestDto;
import org.example.malaquita.entity.Categoria;
import org.example.malaquita.entity.Color;
import org.example.malaquita.entity.Talle;
import org.example.malaquita.entity.Usuario;
import org.example.malaquita.entity.enums.RolUsuario;
import org.example.malaquita.repository.CategoriaRepository;
import org.example.malaquita.repository.ColorRepository;
import org.example.malaquita.repository.ProductoRepository;
import org.example.malaquita.repository.TalleRepository;
import org.example.malaquita.repository.UsuarioRepository;
import org.example.malaquita.service.ProductoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ColorRepository colorRepository;
    private final TalleRepository talleRepository;
    private final ProductoRepository productoRepository;
    private final ProductoService productoService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedCatalogo();
    }

    private void seedAdmin() {
        if (usuarioRepository.findByEmail("admin@malaquita.com").isPresent()) {
            return;
        }
        usuarioRepository.save(Usuario.builder()
                .nombre("Admin")
                .apellido("Malaquita")
                .email("admin@malaquita.com")
                .password(passwordEncoder.encode("Admin123!"))
                .telefono("011-5555-0000")
                .rol(RolUsuario.ADMIN)
                .build());
        log.info("Seed: usuario ADMIN creado (admin@malaquita.com / Admin123!)");
    }

    private void seedCatalogo() {
        if (productoRepository.count() > 0) {
            return;
        }

        Categoria remeras = crearCategoria("Remeras");
        Categoria pantalones = crearCategoria("Pantalones");
        Categoria accesorios = crearCategoria("Accesorios");

        Color negro = crearColor("Negro", "#000000");
        Color blanco = crearColor("Blanco", "#FFFFFF");
        Color gris = crearColor("Gris", "#808080");
        Color azul = crearColor("Azul", "#1E3A8A");
        Color rojo = crearColor("Rojo", "#B91C1C");
        Color verde = crearColor("Verde", "#166534");
        Color beige = crearColor("Beige", "#D6C7A9");

        Talle xs = crearTalle("XS", 1);
        Talle s = crearTalle("S", 2);
        Talle m = crearTalle("M", 3);
        Talle l = crearTalle("L", 4);
        Talle xl = crearTalle("XL", 5);
        Talle unico = crearTalle("ÚNICO", 6);

        productoService.crearProducto(ProductoRequestDto.builder()
                .categoriaId(remeras.getId())
                .nombre("Remera Básica")
                .descripcion("Remera de algodón peinado 24.1, corte regular y cuello redondo.")
                .precioBase(new BigDecimal("24990.00"))
                .variaciones(List.of(
                        variacion(negro, s, 40, "REM-BAS-NEG-S"),
                        variacion(negro, m, 40, "REM-BAS-NEG-M"),
                        variacion(negro, l, 30, "REM-BAS-NEG-L"),
                        variacion(blanco, s, 40, "REM-BAS-BLA-S"),
                        variacion(blanco, m, 40, "REM-BAS-BLA-M"),
                        variacion(gris, m, 25, "REM-BAS-GRI-M"),
                        variacion(gris, xl, 15, "REM-BAS-GRI-XL")))
                .imagenes(List.of(
                        imagen("https://picsum.photos/seed/remera-basica-1/600/750", 1, true),
                        imagen("https://picsum.photos/seed/remera-basica-2/600/750", 2, false)))
                .build());

        productoService.crearProducto(ProductoRequestDto.builder()
                .categoriaId(remeras.getId())
                .nombre("Remera Oversize")
                .descripcion("Corte oversize, tela liviana ideal para uso diario.")
                .precioBase(new BigDecimal("29990.00"))
                .variaciones(List.of(
                        variacion(blanco, xs, 20, "REM-OVS-BLA-XS"),
                        variacion(blanco, s, 30, "REM-OVS-BLA-S"),
                        variacion(blanco, m, 30, "REM-OVS-BLA-M"),
                        variacion(azul, s, 20, "REM-OVS-AZU-S"),
                        variacion(azul, m, 20, "REM-OVS-AZU-M"),
                        variacion(rojo, m, 15, "REM-OVS-ROJ-M"),
                        variacion(rojo, l, 15, "REM-OVS-ROJ-L")))
                .imagenes(List.of(
                        imagen("https://picsum.photos/seed/remera-oversize-1/600/750", 1, true)))
                .build());

        productoService.crearProducto(ProductoRequestDto.builder()
                .categoriaId(pantalones.getId())
                .nombre("Pantalón Cargo")
                .descripcion("Pantalón cargo de gabardina con bolsillos laterales.")
                .precioBase(new BigDecimal("42990.00"))
                .variaciones(List.of(
                        variacion(verde, s, 18, "PAN-CAR-VER-S"),
                        variacion(verde, m, 22, "PAN-CAR-VER-M"),
                        variacion(verde, l, 20, "PAN-CAR-VER-L"),
                        variacion(verde, xl, 12, "PAN-CAR-VER-XL"),
                        variacion(negro, s, 15, "PAN-CAR-NEG-S"),
                        variacion(negro, m, 20, "PAN-CAR-NEG-M")))
                .imagenes(List.of(
                        imagen("https://picsum.photos/seed/pantalon-cargo-1/600/750", 1, true),
                        imagen("https://picsum.photos/seed/pantalon-cargo-2/600/750", 2, false)))
                .build());

        productoService.crearProducto(ProductoRequestDto.builder()
                .categoriaId(accesorios.getId())
                .nombre("Gorro Tejido")
                .descripcion("Gorro de lana tejida, talle único.")
                .precioBase(new BigDecimal("15990.00"))
                .variaciones(List.of(
                        variacion(beige, unico, 35, "GOR-TEJ-BEI-U"),
                        variacion(gris, unico, 35, "GOR-TEJ-GRI-U")))
                .imagenes(List.of(
                        imagen("https://picsum.photos/seed/gorro-tejido-1/600/750", 1, true)))
                .build());

        log.info("Seed: catálogo demo creado (4 productos, 7 categorías/colores/talles)");
    }

    private Categoria crearCategoria(String nombre) {
        return categoriaRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> categoriaRepository.save(Categoria.builder().nombre(nombre).build()));
    }

    private Color crearColor(String nombre, String codigoHex) {
        return colorRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> colorRepository.save(Color.builder()
                        .nombre(nombre.toUpperCase()) 
                        .codigoHex(codigoHex)
                        .build()));
    }

    private Talle crearTalle(String nombre, Integer orden) {
        return talleRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> talleRepository.save(Talle.builder()
                        .nombre(nombre.toUpperCase())
                        .orden(orden)
                        .build()));
    }

    private VariacionRequestDto variacion(Color color, Talle talle, Integer stock, String sku) {
        return VariacionRequestDto.builder()
                .colorId(color.getId())
                .talleId(talle.getId())
                .stock(stock)
                .sku(sku)
                .build();
    }

    private ImagenRequestDto imagen(String url, Integer orden, Boolean esPrincipal) {
        return ImagenRequestDto.builder()
                .urlImagen(url)
                .orden(orden)
                .esPrincipal(esPrincipal)
                .build();
    }
}