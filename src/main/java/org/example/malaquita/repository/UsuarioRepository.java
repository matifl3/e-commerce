package org.example.malaquita.repository;

import org.example.malaquita.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT YEAR(u.fechaCreacion) AS anio, MONTH(u.fechaCreacion) AS mes, COUNT(u) "
            + "FROM Usuario u GROUP BY YEAR(u.fechaCreacion), MONTH(u.fechaCreacion) "
            + "ORDER BY YEAR(u.fechaCreacion), MONTH(u.fechaCreacion)")
    List<Object[]> countNuevosPorMes();
}