package com.example.direccion.config;

import com.example.direccion.model.Region;
import com.example.direccion.Repository.ComunaRepository;
import com.example.direccion.Repository.DireccionRepository;
import com.example.direccion.Repository.RegionRepository;
import com.example.direccion.model.Comuna;
import com.example.direccion.model.Direccion;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ComunaRepository comunaRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @PostConstruct
    public void cargarDatosIniciales() {
        // Crea regiones
        Region r1 = new Region(null, "Región Metropolitana");
        Region r2 = new Region(null, "Región de Valparaíso");
        regionRepository.save(r1);
        regionRepository.save(r2);

        // Crea comunas
        Comuna c1 = new Comuna(null, "Santiago", r1);
        Comuna c2 = new Comuna(null, "Providencia", r1);
        Comuna c3 = new Comuna(null, "Valparaíso", r2);
        comunaRepository.save(c1);
        comunaRepository.save(c2);
        comunaRepository.save(c3);

        // Crea direcciones
        Direccion d1 = new Direccion(null, 101L, c1, r1, "Av. O’Higgins", "123", "Dpto 1", "8320000");
        Direccion d2 = new Direccion(null, 102L, c2, r1, "Av. Providencia", "456", null, "7500000");
        Direccion d3 = new Direccion(null, 103L, c3, r2, "Calle Condell", "789", "3B", "2340000");
        direccionRepository.save(d1);
        direccionRepository.save(d2);
        direccionRepository.save(d3);
    }
}