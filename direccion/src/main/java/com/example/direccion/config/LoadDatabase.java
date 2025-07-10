package com.example.direccion.config;

import com.example.direccion.model.Region;
import com.example.direccion.Repository.ComunaRepository;
import com.example.direccion.Repository.DireccionRepository;
import com.example.direccion.Repository.RegionRepository;
import com.example.direccion.model.Comuna;
import com.example.direccion.model.Direccion;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    @Bean
    public CommandLineRunner initDatabase(
            RegionRepository regionRepository,
            ComunaRepository comunaRepository,
            DireccionRepository direccionRepository
    ) {
        return args -> {
            if (regionRepository.count() > 0) return;

            // ========================
            // 1. Regiones
            // ========================
            Region r1 = new Region(null, "Región Metropolitana");
            Region r2 = new Region(null, "Región de Valparaíso");
            Region r3 = new Region(null, "Región del Biobío");
            Region r4 = new Region(null, "Región de Coquimbo");
            Region r5 = new Region(null, "Región de Los Lagos");
            regionRepository.save(r1);
            regionRepository.save(r2);
            regionRepository.save(r3);
            regionRepository.save(r4);
            regionRepository.save(r5);

            // ========================
            // 2. Comunas
            // ========================
            Comuna c1 = new Comuna(null, "Santiago", r1);
            Comuna c2 = new Comuna(null, "Providencia", r1);
            Comuna c3 = new Comuna(null, "Valparaíso", r2);
            Comuna c4 = new Comuna(null, "La Serena", r4);
            Comuna c5 = new Comuna(null, "Concepcion", r5);
            comunaRepository.save(c1);
            comunaRepository.save(c2);
            comunaRepository.save(c3);
            comunaRepository.save(c4);
            comunaRepository.save(c5);

            // ========================
            // 3. Direcciones
            // ========================
            Direccion d1 = new Direccion(null, 1L, c1, r1, "Av. Libertador Bernardo O’Higgins", "1234", "Depto 101", "8320000");
            Direccion d2 = new Direccion(null, 2L, c2, r1, "Av. Providencia", "5678", null, "7500000");
            Direccion d3 = new Direccion(null, 3L, c3, r2, "Calle Esmeralda", "234", "Oficina 2", "2340000");
            Direccion d4 = new Direccion(null, 4L, c4, r3, "Av. Los Carrera", "456", "Casa 3", "4030000");
            Direccion d5 = new Direccion(null, 5L, c5, r4, "Calle Colón", "789", null, "1720000");
            direccionRepository.save(d1);
            direccionRepository.save(d2);
            direccionRepository.save(d3);
            direccionRepository.save(d4);
            direccionRepository.save(d5);
        };
    }
}
