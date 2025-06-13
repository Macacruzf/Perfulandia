package com.example.gestionventas.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.gestionventas.model.DetalleVenta;


@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long>{
    List<DetalleVenta> findByIdVenta(Long ventaId);
    List<DetalleVenta> findByIdProducto(Long idProducto);
}
