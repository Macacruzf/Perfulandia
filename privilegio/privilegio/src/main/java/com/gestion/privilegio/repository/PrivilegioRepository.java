
package com.gestion.privilegio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.privilegio.model.Privilegio;

public interface PrivilegioRepository  extends JpaRepository<Privilegio, Long>{
    List<Privilegio> findByNombreRol(String nombreRol);
   

}
