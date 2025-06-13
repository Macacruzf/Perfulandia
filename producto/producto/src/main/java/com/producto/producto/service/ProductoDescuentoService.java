package com.producto.producto.service;

import com.producto.producto.webclient.PromocionClient;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class ProductoDescuentoService {

    private final PromocionClient promocionClient;

    public ProductoDescuentoService(PromocionClient promocionClient) {
        this.promocionClient = promocionClient;
    }

    public List<Map<String, Object>> obtenerDescuentosPorProducto(Long idProducto) {
        return promocionClient.getDescuentosByProductoId(idProducto);
    }
}