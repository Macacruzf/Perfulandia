package com.example.direccion.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.direccion.model.Direccion;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
       List<Direccion> findByIdUser(Long idUser);
}
