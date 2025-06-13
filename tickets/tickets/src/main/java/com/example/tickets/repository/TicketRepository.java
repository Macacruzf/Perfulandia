package com.example.tickets.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tickets.model.Ticket;

@Repository

public interface TicketRepository extends JpaRepository<Ticket, Long>{

    List<Ticket> findByIdUsers(Long idUsuario);
}



