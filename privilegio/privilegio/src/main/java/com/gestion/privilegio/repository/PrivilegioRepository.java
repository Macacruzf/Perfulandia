package com.gestion.privilegio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestion.privilegio.model.Modulo;
import com.gestion.privilegio.model.Privilegio;
@Repository
public interface PrivilegioRepository extends JpaRepository<Privilegio, Long>{
    @Query("SELECT p.modulo FROM Privilegio p WHERE p.nombreRol = :nombreRol")
    List<Modulo> findModulosByRolNombre(@Param("nombreRol") String nombreRol);
    
    /**
     * Verifica si un determinado rol tiene acceso a un módulo específico.
     * Muy útil para validaciones rápidas de autorización desde el microservicio autenticado.
     *
     * @param rol el nombre del rol
     * @param modulo el módulo a verificar
     * @return true si el rol tiene privilegios sobre ese módulo
     */
    boolean existsByRolAndModulo(String rol, Modulo modulo);
}
