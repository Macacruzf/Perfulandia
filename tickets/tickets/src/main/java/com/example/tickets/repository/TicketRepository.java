package com.example.tickets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tickets.model.Ticket;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // Puedes agregar consultas personalizadas si es necesario
}



