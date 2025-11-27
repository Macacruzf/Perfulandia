package com.example.tickets.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.tickets.model.Mensaje;
import com.example.tickets.model.Motivo;
import com.example.tickets.model.Ticket;
import com.example.tickets.repository.MensajeRepository;
import com.example.tickets.repository.MotivoRepository;
import com.example.tickets.repository.TicketRepository;

@Configuration
public class LoadDataBase {

    @Bean
    public CommandLineRunner initTicketDatabase(
            MotivoRepository motivoRepository,
            TicketRepository ticketRepository,
            MensajeRepository mensajeRepository
    ) {
        return args -> {
            if (motivoRepository.count() > 0) return;

            // =========================
            // 1. Crear motivos
            // =========================
            Motivo m1 = motivoRepository.save(new Motivo(null, "Falla técnica"));
            Motivo m2 = motivoRepository.save(new Motivo(null, "Consulta general"));
            Motivo m3 = motivoRepository.save(new Motivo(null, "Problema de facturación"));
            Motivo m4 = motivoRepository.save(new Motivo(null, "Problemas con el sistema"));
            Motivo m5 = motivoRepository.save(new Motivo(null, "Otro"));

            // =========================
            // 2. Crear tickets
            // =========================
            Ticket t1 = ticketRepository.save(Ticket.builder()
                    .fechaCreacion(LocalDateTime.now().minusDays(2))
                    .idUsuario(1L)
                    .comentario("La impresora no enciende.")
                    .motivo(m1)
                    .build());

            Ticket t2 = ticketRepository.save(Ticket.builder()
                    .fechaCreacion(LocalDateTime.now().minusDays(1))
                    .idUsuario(2L)
                    .comentario("¿Cómo puedo cambiar mi contraseña?")
                    .motivo(m2)
                    .build());

            Ticket t3 = ticketRepository.save(Ticket.builder()
                    .fechaCreacion(LocalDateTime.now())
                    .idUsuario(3L)
                    .comentario("Se me cobró doble en la última boleta.")
                    .motivo(m3)
                    .build());

            Ticket t4 = ticketRepository.save(Ticket.builder()
                    .fechaCreacion(LocalDateTime.now().minusHours(5))
                    .idUsuario(4L)
                    .comentario("El sistema no carga los informes.")
                    .motivo(m4)
                    .build());

            Ticket t5 = ticketRepository.save(Ticket.builder()
                    .fechaCreacion(LocalDateTime.now().minusHours(2))
                    .idUsuario(5L)
                    .comentario("Quiero actualizar mis datos de contacto.")
                    .motivo(m5)
                    .build());

            // =========================
            // 3. Crear mensajes para cada ticket
            // =========================
            List<Ticket> tickets = List.of(t1, t2, t3, t4, t5);

            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);

                mensajeRepository.save(Mensaje.builder()
                        .fechaMensaje(ticket.getFechaCreacion().plusMinutes(30))
                        .mensaje("Mensaje inicial del usuario.")
                        .tipoMensaje("comentario")
                        .ticket(ticket)
                        .idUsuario(ticket.getIdUsuario())
                        .build());

                mensajeRepository.save(Mensaje.builder()
                        .fechaMensaje(ticket.getFechaCreacion().plusHours(1))
                        .mensaje("Respuesta del soporte técnico.")
                        .tipoMensaje("respuesta")
                        .ticket(ticket)
                        .idUsuario(99L) // Suponiendo que el usuario 99 es un agente
                        .build());
            }
        };
    }
}
