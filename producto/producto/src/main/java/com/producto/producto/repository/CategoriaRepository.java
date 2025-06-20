package com.producto.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.producto.producto.model.Categoria;


@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Ejemplo de método adicional que podrías agregar:
    // Optional<Categoria> findByNombre(String nombre);
}
