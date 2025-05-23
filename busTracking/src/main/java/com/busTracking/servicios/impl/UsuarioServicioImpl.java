package com.busTracking.servicios.impl;

import com.busTracking.modelo.entidades.Usuario;
import com.busTracking.repositorios.UsuarioRepositorio;
import com.busTracking.servicios.interfaces.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServicioImpl implements UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;

    @Autowired
    public UsuarioServicioImpl(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepositorio.findAll() ;
    }

    @Override
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepositorio.findById(id) ;
    }

    @Override
    public Optional<Usuario> obtenerUsuarioPorEmail(String email) {
        return usuarioRepositorio.findByEmail(email);
    }

    @Override
    public List<Usuario> obtenerUsuariosPorNombre(String nombre) {return usuarioRepositorio.findByNombre(nombre);}

    @Override
    public List<Usuario> obtenerUsuariosPorApellido(String apellido) {return usuarioRepositorio.findByApellido(apellido);}

    @Override
    public Usuario crearUsuario(Usuario usuario) {

            if (usuario.getNombre() == null || usuario.getNombre().isEmpty()) {
                throw new RuntimeException("El nombre es obligatorio");
            }
            if (usuario.getApellido() == null || usuario.getApellido().isEmpty()) {
                throw new RuntimeException("El apellido es obligatorio");
            }
            if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
                throw new RuntimeException("El email es obligatorio");
            }
            if (!usuario.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                throw new RuntimeException("El formato del email es incorrecto");
            }
            if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
                throw new RuntimeException("La contraseña es obligatoria");
            }
            if (usuario.getPassword().length() < 4) {
                throw new RuntimeException("La contraseña debe tener al menos 4 caracteres");
            }
            if (usuarioRepositorio.findByEmail(usuario.getEmail()).isPresent()) {
                throw new RuntimeException("El email ya está en uso");
            }
            return usuarioRepositorio.save(usuario);
        }


    @Override
    public Usuario actualizarUsuario(Long id, Usuario usuario) {

        if (usuarioRepositorio.findById(id).isEmpty()) {
            throw new RuntimeException("El usuario no existe");
        }

        Usuario usuarioExistente = usuarioRepositorio.findById(id).get();
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setApellido(usuario.getApellido());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setPassword(usuario.getPassword());

        // Verificar si el email ha cambiado y si ya está en uso
        if (!usuarioExistente.getEmail().equals(usuario.getEmail()) && usuarioRepositorio.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está en uso");
        }

        return usuarioRepositorio.save(usuarioExistente);
    }

    @Override
    public void eliminarUsuario(Long id) {
        usuarioRepositorio.deleteById(id);
    }
}
