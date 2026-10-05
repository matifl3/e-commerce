package org.example.malaquita.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "producto_variacion",
       uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "color_id", "talle_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoVariacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "talle_id", nullable = false)
    private Talle talle;

    @NotNull
    @Column(nullable = false)
    private Integer stock;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String sku;
}