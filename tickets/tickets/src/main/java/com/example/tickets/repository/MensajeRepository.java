package com.example.tickets.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tickets.model.Mensaje;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long>{
    List<Mensaje> findByTicket_IdTicket(Long idTicket);

}