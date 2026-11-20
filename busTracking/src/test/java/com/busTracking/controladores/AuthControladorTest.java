package com.busTracking.controladores;

import com.busTracking.excepciones.CredencialesInvalidasException;
import com.busTracking.excepciones.EmailEnUsoException;
import com.busTracking.modelo.dto.LoginRequest;
import com.busTracking.modelo.dto.RegistroRequest;
import com.busTracking.modelo.dto.UsuarioDTO;
import com.busTracking.servicios.interfaces.UsuarioServicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthControlador.class)
class AuthControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioServicio usuarioServicio;

    @Test
    void login_correcto_devuelve200SinContrasena() throws Exception {
        when(usuarioServicio.login(any(LoginRequest.class)))
                .thenReturn(new UsuarioDTO(1L, "Ana", "López", "ana@test.com", false));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@test.com\",\"password\":\"secreta123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@test.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void login_incorrecto_devuelve401ConMensaje() throws Exception {
        when(usuarioServicio.login(any(LoginRequest.class))).thenThrow(new CredencialesInvalidasException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@test.com\",\"password\":\"mala\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Email o contraseña incorrectos"));
    }

    @Test
    void registro_correcto_devuelve201() throws Exception {
        when(usuarioServicio.registrar(any(RegistroRequest.class)))
                .thenReturn(new UsuarioDTO(2L, "Luis", "Pérez", "luis@test.com", false));

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Luis\",\"apellido\":\"Pérez\",\"email\":\"luis@test.com\",\"password\":\"secreta123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void registro_conEmailRepetido_devuelve409() throws Exception {
        when(usuarioServicio.registrar(any(RegistroRequest.class)))
                .thenThrow(new EmailEnUsoException("luis@test.com"));

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Luis\",\"apellido\":\"Pérez\",\"email\":\"luis@test.com\",\"password\":\"secreta123\"}"))
                .andExpect(status().isConflict());
    }
}
