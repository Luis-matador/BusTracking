package com.busTracking.servicios.interfaces;

import com.busTracking.modelo.entidades.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioServicio {

    List<Usuario> obtenerTodosLosUsuarios();

    Optional<Usuario> obtenerUsuarioPorId(Long id);

    Optional<Usuario> obtenerUsuarioPorEmail(String email);

    List<Usuario> obtenerUsuariosPorNombre(String nombre);

    List<Usuario> obtenerUsuariosPorApellido(String apellido);

    Usuario crearUsuario(Usuario usuario);

    Usuario actualizarUsuario(Long id, Usuario usuario);

    void eliminarUsuario(Long id);

}
