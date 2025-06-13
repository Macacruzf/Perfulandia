package com.example.tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tickets.model.Motivo;

@Repository

public interface MotivoRepository extends JpaRepository<Motivo, Long>{

}

