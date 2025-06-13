
package com.gestion.usuario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestion.usuario.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>  {
     Optional<Usuario> findByCorreo(String correo);
     Optional<Usuario> findByNickname(String nickname);

}

