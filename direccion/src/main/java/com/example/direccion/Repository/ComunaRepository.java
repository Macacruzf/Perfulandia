package com.example.direccion.Repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.direccion.model.Comuna;


public interface ComunaRepository extends JpaRepository<Comuna, Long> {
    List<Comuna> findByRegion_IdRegion(Long idRegion);

}
