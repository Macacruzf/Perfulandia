package com.example.gestionventas.config;
import com.example.gestionventas.model.Venta;
import com.example.gestionventas.model.DetalleVenta;
import com.example.gestionventas.Repository.VentaRepository;
import com.example.gestionventas.Repository.DetalleVentaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;


@Configuration
public class LoadDataBase {

    @Bean
    public CommandLineRunner initDatabase(VentaRepository ventaRepo, DetalleVentaRepository detalleRepo) {
        return args -> {
            if (ventaRepo.count() == 0) {
                // ========================
                // Venta 1 con 2 detalles
                // ========================
                Venta venta1 = new Venta();
                venta1.setIdUsuario(1L);
                venta1.setIdDireccion(10L);
                venta1.setFechaVenta(LocalDateTime.now().minusDays(1));
                venta1.setTotal(24980); // 19980 + 5000
                venta1 = ventaRepo.save(venta1);

                DetalleVenta detalle1_1 = new DetalleVenta();
                detalle1_1.setIdProducto(1001L);
                detalle1_1.setCantidad(2);
                detalle1_1.setPrecioUnitario(9990);
                detalle1_1.setSubtotal(19980);
                detalle1_1.setVenta(venta1);
                detalleRepo.save(detalle1_1);

                DetalleVenta detalle1_2 = new DetalleVenta();
                detalle1_2.setIdProducto(1002L);
                detalle1_2.setCantidad(1);
                detalle1_2.setPrecioUnitario(5000);
                detalle1_2.setSubtotal(5000);
                detalle1_2.setVenta(venta1);
                detalleRepo.save(detalle1_2);

                // ========================
                // Venta 2 con 1 detalle
                // ========================
                Venta venta2 = new Venta();
                venta2.setIdUsuario(2L);
                venta2.setIdDireccion(20L);
                venta2.setFechaVenta(LocalDateTime.now());
                venta2.setTotal(14990);
                venta2 = ventaRepo.save(venta2);

                DetalleVenta detalle2_1 = new DetalleVenta();
                detalle2_1.setIdProducto(1003L);
                detalle2_1.setCantidad(1);
                detalle2_1.setPrecioUnitario(14990);
                detalle2_1.setSubtotal(14990);
                detalle2_1.setVenta(venta2);
                detalleRepo.save(detalle2_1);
            }
        };
    }
}