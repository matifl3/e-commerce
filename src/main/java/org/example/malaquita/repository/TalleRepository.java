package org.example.malaquita.repository;

import org.example.malaquita.entity.Talle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TalleRepository extends JpaRepository<Talle, Long> {

    Optional<Talle> findByNombreIgnoreCase(String nombre);
}