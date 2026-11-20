package com.busTracking.servicios.interfaces;

import com.busTracking.modelo.dto.LoginRequest;
import com.busTracking.modelo.dto.RegistroRequest;
import com.busTracking.modelo.dto.UsuarioDTO;

import java.util.List;

public interface UsuarioServicio {

    List<UsuarioDTO> obtenerTodosLosUsuarios();

    UsuarioDTO obtenerUsuarioPorId(Long id);

    UsuarioDTO obtenerUsuarioPorEmail(String email);

    List<UsuarioDTO> obtenerUsuariosPorNombre(String nombre);

    List<UsuarioDTO> obtenerUsuariosPorApellido(String apellido);

    UsuarioDTO registrar(RegistroRequest request);

    UsuarioDTO login(LoginRequest request);

    UsuarioDTO actualizarUsuario(Long id, RegistroRequest request);

    void eliminarUsuario(Long id);
}
