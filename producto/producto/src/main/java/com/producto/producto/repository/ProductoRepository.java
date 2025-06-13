package com.producto.producto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.producto.producto.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
// Método corregido: busca por nombre (ignorando mayúsculas/minúsculas)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCategoria_IdCategoria(Long idCategoria); // ✅ Antes: findByIdCategoria

    List<Producto> findByNombreContainingIgnoreCaseAndCategoria_IdCategoria(String nombre, Long idCategoria); // ✅ Antes: ...AndIdCategoria

    List<Producto> findByIdEstado(Long idEstado);
    
    List<Producto> findByIdProductoIn(List<Long> idProductos);

    boolean existsByCategoria_IdCategoria(Long idCategoria);
}
