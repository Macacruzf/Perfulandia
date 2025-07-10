package com.producto.producto.config;


import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.producto.producto.model.Categoria;
import com.producto.producto.model.EstadoProducto;
import com.producto.producto.model.Producto;
import com.producto.producto.repository.CategoriaRepository;
import com.producto.producto.repository.ProductoRepository;

@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(CategoriaRepository categoriaRepo, ProductoRepository productoRepo) {
        return args -> {
            System.out.println("=== Iniciando carga de datos de prueba ===");

            if (categoriaRepo.count() > 0 || productoRepo.count() > 0) {
                System.out.println("Datos ya existen. No se cargan duplicados.");
                return;
            }

            // Categorías
            Categoria perfumeFemenino = new Categoria();
            perfumeFemenino.setNombre("Perfume Femenino");
            perfumeFemenino.setDescripcion("Fragancias diseñadas para mujeres.");

            Categoria perfumeMasculino = new Categoria();
            perfumeMasculino.setNombre("Perfume Masculino");
            perfumeMasculino.setDescripcion("Aromas intensos para hombres.");

            Categoria unisex = new Categoria();
            unisex.setNombre("Perfume Unisex");
            unisex.setDescripcion("Fragancias neutras.");

            Categoria colonias = new Categoria();
            colonias.setNombre("Colonias");
            colonias.setDescripcion("Aromas ligeros para uso diario.");

            categoriaRepo.saveAll(List.of(perfumeFemenino, perfumeMasculino, unisex, colonias));
            System.out.println("Categorías guardadas");

            // Productos
            Producto p1 = new Producto(null, "Chanel N°5", "...", 129990, 30, EstadoProducto.ACTIVO, perfumeFemenino);
            Producto p2 = new Producto(null, "Dior Sauvage", "...", 109990, 20, EstadoProducto.ACTIVO, perfumeMasculino);
            Producto p3 = new Producto(null, "CK One", "...", 79990, 50, EstadoProducto.ACTIVO, unisex);
            Producto p4 = new Producto(null, "Colonia Adidas Sport", "...", 19990, 60, EstadoProducto.ACTIVO, colonias);
            Producto p5 = new Producto(null, "Axe Body Spray", "...", 9990, 100, EstadoProducto.ACTIVO, colonias);

            productoRepo.saveAll(List.of(p1, p2, p3, p4, p5));
            System.out.println("Productos guardados correctamente");
        };
    }
}