
package com.gestion.privilegio.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.privilegio.model.Modulo;

public interface ModuloRepository extends JpaRepository<Modulo, Long> {
     
     Optional<Modulo> findByNombreModulo(String nombreModulo);

}

