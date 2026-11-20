package com.busTracking.controladores;

import com.busTracking.modelo.dto.RegistroRequest;
import com.busTracking.modelo.dto.UsuarioDTO;
import com.busTracking.servicios.interfaces.UsuarioServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    public UsuarioControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        return usuarioServicio.obtenerTodosLosUsuarios();
    }

    @GetMapping("/{id}")
    public UsuarioDTO obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioServicio.obtenerUsuarioPorId(id);
    }

    @GetMapping("/email/{email}")
    public UsuarioDTO obtenerUsuarioPorEmail(@PathVariable String email) {
        return usuarioServicio.obtenerUsuarioPorEmail(email);
    }

    @GetMapping("/nombre/{nombre}")
    public List<UsuarioDTO> obtenerUsuariosPorNombre(@PathVariable String nombre) {
        return usuarioServicio.obtenerUsuariosPorNombre(nombre);
    }

    @GetMapping("/apellido/{apellido}")
    public List<UsuarioDTO> obtenerUsuariosPorApellido(@PathVariable String apellido) {
        return usuarioServicio.obtenerUsuariosPorApellido(apellido);
    }

    @PutMapping("/{id}")
    public UsuarioDTO actualizarUsuario(@PathVariable Long id, @RequestBody RegistroRequest request) {
        return usuarioServicio.actualizarUsuario(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioServicio.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
