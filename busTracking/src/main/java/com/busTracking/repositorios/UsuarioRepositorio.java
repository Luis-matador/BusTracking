package com.busTracking.repositorios;

import com.busTracking.modelo.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    List<Usuario> findByNombre(String nombre);

    List<Usuario> findByApellido(String apellido);

    Optional<Usuario> findByEmail(String email);


}
