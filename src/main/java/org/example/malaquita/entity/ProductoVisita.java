package org.example.malaquita.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "producto_visita")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoVisita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "fecha_visita", nullable = false, updatable = false)
    private LocalDateTime fechaVisita;

    @Column(name = "sesion_id", length = 255)
    private String sesionId;

    @PrePersist
    protected void prePersist() {
        this.fechaVisita = LocalDateTime.now();
    }
}