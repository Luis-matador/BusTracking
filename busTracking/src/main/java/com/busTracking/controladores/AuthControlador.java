package com.busTracking.controladores;

import com.busTracking.modelo.dto.LoginRequest;
import com.busTracking.modelo.dto.RegistroRequest;
import com.busTracking.modelo.dto.UsuarioDTO;
import com.busTracking.servicios.interfaces.UsuarioServicio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Registro e inicio de sesión. La comprobación de credenciales se hace siempre en el servidor.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthControlador {

    private final UsuarioServicio usuarioServicio;

    public AuthControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDTO registrar(@RequestBody RegistroRequest request) {
        return usuarioServicio.registrar(request);
    }

    @PostMapping("/login")
    public UsuarioDTO login(@RequestBody LoginRequest request) {
        return usuarioServicio.login(request);
    }
}
