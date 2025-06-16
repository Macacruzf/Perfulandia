package com.example.direccion.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.direccion.model.Direccion;
@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
       List<Direccion> findByIdUsuario(Long idUsuario);
       List<Direccion> findByComuna_IdComuna(Long idComuna);
}
