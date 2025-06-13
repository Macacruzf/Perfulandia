
package com.gestion.privilegio.service;


import java.util.List;


import org.springframework.stereotype.Service;

import com.gestion.privilegio.model.Privilegio;
import com.gestion.privilegio.model.Usuario;
import com.gestion.privilegio.repository.PrivilegioRepository;
import com.gestion.privilegio.webclient.AutenticacionClient;


@Service
public class PrivilegioServiceImpl implements PrivilegioService {
    private final PrivilegioRepository privilegioRepository;
    private final AutenticacionClient autenticacionClient;

    public PrivilegioServiceImpl(PrivilegioRepository privilegioRepository, AutenticacionClient autenticacionClient) {
        this.privilegioRepository = privilegioRepository;
        this.autenticacionClient = autenticacionClient;
    }

    @Override
    public List<Privilegio> obtenerPrivilegiosPorRol(String nombreRol) {
        return privilegioRepository.findByNombreRol(nombreRol);
    }

    @Override
    public String obtenerMensajePorCredenciales(String nickname, String password) {
        Usuario usuario = autenticacionClient.autenticar(nickname, password);

        if (usuario == null) {
            return "Credenciales inválidas o usuario no encontrado.";
        }

        String rol = (usuario.getRol() != null) ? usuario.getRol().getNombreRol() : null;

        if (rol == null) {
            return "Usuario sin rol asignado.";
        }

        switch (rol) {
            case "Admin":
                return "Usuario Admin: puede administrar todo el sistema.";
            case "Cliente":
                return "Usuario Cliente: puede realizar compras y ver sus pedidos.";
            case "Gerente":
                return "Usuario Gerente: puede supervisar ventas y gestionar inventarios.";
            case "Logística":
                return "Usuario Logística: puede gestionar envíos y estados de pedidos.";
            default:
                return "Rol no reconocido.";
        }
    }
}
