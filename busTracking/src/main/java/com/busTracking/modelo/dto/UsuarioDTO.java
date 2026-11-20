package com.busTracking.modelo.dto;

import com.busTracking.modelo.entidades.Usuario;

/**
 * Datos públicos de un usuario. Nunca incluye la contraseña.
 */
public record UsuarioDTO(Long id, String nombre, String apellido, String email, boolean admin) {

    public static UsuarioDTO desde(Usuario usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getNombre(), usuario.getApellido(),
                usuario.getEmail(), usuario.isAdmin());
    }
}
