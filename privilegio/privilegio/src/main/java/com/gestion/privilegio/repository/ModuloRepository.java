
package com.gestion.privilegio.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion.privilegio.model.Modulo;
@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
     
    /**
     * Busca un módulo por su nombre.
     * @param nombre nombre del módulo
     * @return módulo correspondiente (si existe)
     */
    Optional<Modulo> findByNombre(String nombre);

}

