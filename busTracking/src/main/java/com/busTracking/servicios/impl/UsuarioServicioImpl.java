package com.busTracking.servicios.impl;

import com.busTracking.excepciones.CredencialesInvalidasException;
import com.busTracking.excepciones.DatosInvalidosException;
import com.busTracking.excepciones.EmailEnUsoException;
import com.busTracking.excepciones.RecursoNoEncontradoException;
import com.busTracking.modelo.dto.LoginRequest;
import com.busTracking.modelo.dto.RegistroRequest;
import com.busTracking.modelo.dto.UsuarioDTO;
import com.busTracking.modelo.entidades.Usuario;
import com.busTracking.repositorios.UsuarioRepositorio;
import com.busTracking.servicios.interfaces.UsuarioServicio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UsuarioServicioImpl implements UsuarioServicio {

    private static final Pattern EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final int LONGITUD_MINIMA_PASSWORD = 6;

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final String codigoAdmin;

    public UsuarioServicioImpl(UsuarioRepositorio usuarioRepositorio,
                               PasswordEncoder passwordEncoder,
                               @Value("${app.admin.codigo:}") String codigoAdmin) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.codigoAdmin = codigoAdmin;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        return usuarioRepositorio.findAll().stream().map(UsuarioDTO::desde).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        return UsuarioDTO.desde(buscarPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioPorEmail(String email) {
        return usuarioRepositorio.findByEmail(email)
                .map(UsuarioDTO::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún usuario con email " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerUsuariosPorNombre(String nombre) {
        return usuarioRepositorio.findByNombre(nombre).stream().map(UsuarioDTO::desde).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> obtenerUsuariosPorApellido(String apellido) {
        return usuarioRepositorio.findByApellido(apellido).stream().map(UsuarioDTO::desde).toList();
    }

    @Override
    @Transactional
    public UsuarioDTO registrar(RegistroRequest request) {
        validarDatosBasicos(request.nombre(), request.apellido(), request.email());
        validarPassword(request.password());
        if (usuarioRepositorio.findByEmail(request.email()).isPresent()) {
            throw new EmailEnUsoException(request.email());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre().trim());
        usuario.setApellido(request.apellido().trim());
        usuario.setEmail(request.email().trim().toLowerCase());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setAdmin(esCodigoAdminValido(request.codigoAdmin()));

        return UsuarioDTO.desde(usuarioRepositorio.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO login(LoginRequest request) {
        if (request.email() == null || request.password() == null) {
            throw new CredencialesInvalidasException();
        }
        Usuario usuario = usuarioRepositorio.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(CredencialesInvalidasException::new);
        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new CredencialesInvalidasException();
        }
        return UsuarioDTO.desde(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, RegistroRequest request) {
        Usuario usuario = buscarPorId(id);
        validarDatosBasicos(request.nombre(), request.apellido(), request.email());

        String nuevoEmail = request.email().trim().toLowerCase();
        if (!usuario.getEmail().equalsIgnoreCase(nuevoEmail)
                && usuarioRepositorio.findByEmail(nuevoEmail).isPresent()) {
            throw new EmailEnUsoException(nuevoEmail);
        }

        usuario.setNombre(request.nombre().trim());
        usuario.setApellido(request.apellido().trim());
        usuario.setEmail(nuevoEmail);
        // La contraseña solo se cambia si se envía una nueva
        if (request.password() != null && !request.password().isBlank()) {
            validarPassword(request.password());
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }
        return UsuarioDTO.desde(usuarioRepositorio.save(usuario));
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioRepositorio.delete(buscarPorId(id));
    }

    private Usuario buscarPorId(Long id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }

    private boolean esCodigoAdminValido(String codigo) {
        return codigo != null && !codigoAdmin.isBlank() && codigoAdmin.equals(codigo);
    }

    private void validarDatosBasicos(String nombre, String apellido, String email) {
        if (nombre == null || nombre.isBlank()) {
            throw new DatosInvalidosException("El nombre es obligatorio");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new DatosInvalidosException("El apellido es obligatorio");
        }
        if (email == null || !EMAIL.matcher(email.trim()).matches()) {
            throw new DatosInvalidosException("El formato del email es incorrecto");
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < LONGITUD_MINIMA_PASSWORD) {
            throw new DatosInvalidosException(
                    "La contraseña debe tener al menos " + LONGITUD_MINIMA_PASSWORD + " caracteres");
        }
    }
}
