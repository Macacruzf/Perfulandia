
package com.gestion.privilegio.service;

import java.util.List;

import com.gestion.privilegio.model.Privilegio;

public interface PrivilegioService {
    List<Privilegio> obtenerPrivilegiosPorRol(String nombreRol);
    String obtenerMensajePorCredenciales(String nickname, String password);

}
