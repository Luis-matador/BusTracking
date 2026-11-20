package com.busTracking.servicios;

import com.busTracking.excepciones.CredencialesInvalidasException;
import com.busTracking.excepciones.DatosInvalidosException;
import com.busTracking.excepciones.EmailEnUsoException;
import com.busTracking.excepciones.RecursoNoEncontradoException;
import com.busTracking.modelo.dto.LoginRequest;
import com.busTracking.modelo.dto.RegistroRequest;
import com.busTracking.modelo.dto.UsuarioDTO;
import com.busTracking.modelo.entidades.Usuario;
import com.busTracking.repositorios.UsuarioRepositorio;
import com.busTracking.servicios.impl.UsuarioServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServicioImplTest {

    private static final String CODIGO_ADMIN = "codigo-secreto";

    @Mock
    private UsuarioRepositorio usuarioRepositorio;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UsuarioServicioImpl servicio;

    @BeforeEach
    void setUp() {
        servicio = new UsuarioServicioImpl(usuarioRepositorio, passwordEncoder, CODIGO_ADMIN);
    }

    private Usuario usuarioGuardado(String email, String passwordPlano) {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNombre("Ana");
        u.setApellido("López");
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(passwordPlano));
        return u;
    }

    @Test
    void registrar_guardaLaContrasenaCifradaConBCrypt() {
        when(usuarioRepositorio.findByEmail("ana@test.com")).thenReturn(Optional.empty());
        when(usuarioRepositorio.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        servicio.registrar(new RegistroRequest("Ana", "López", "ana@test.com", "secreta123", null));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepositorio).save(captor.capture());
        String guardada = captor.getValue().getPassword();
        assertThat(guardada).isNotEqualTo("secreta123");
        assertThat(passwordEncoder.matches("secreta123", guardada)).isTrue();
    }

    @Test
    void registrar_conCodigoAdminCorrecto_creaAdministrador() {
        when(usuarioRepositorio.findByEmail(any())).thenReturn(Optional.empty());
        when(usuarioRepositorio.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO dto = servicio.registrar(
                new RegistroRequest("Ana", "López", "ana@test.com", "secreta123", CODIGO_ADMIN));

        assertThat(dto.admin()).isTrue();
    }

    @Test
    void registrar_conCodigoAdminIncorrecto_creaUsuarioNormal() {
        when(usuarioRepositorio.findByEmail(any())).thenReturn(Optional.empty());
        when(usuarioRepositorio.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO dto = servicio.registrar(
                new RegistroRequest("Ana", "López", "ana@test.com", "secreta123", "1234"));

        assertThat(dto.admin()).isFalse();
    }

    @Test
    void registrar_sinCodigoConfigurado_nadiePuedeSerAdmin() {
        UsuarioServicioImpl sinCodigo = new UsuarioServicioImpl(usuarioRepositorio, passwordEncoder, "");
        when(usuarioRepositorio.findByEmail(any())).thenReturn(Optional.empty());
        when(usuarioRepositorio.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO dto = sinCodigo.registrar(
                new RegistroRequest("Ana", "López", "ana@test.com", "secreta123", ""));

        assertThat(dto.admin()).isFalse();
    }

    @Test
    void registrar_conEmailRepetido_lanzaExcepcion() {
        when(usuarioRepositorio.findByEmail("ana@test.com"))
                .thenReturn(Optional.of(usuarioGuardado("ana@test.com", "secreta123")));

        assertThatThrownBy(() -> servicio.registrar(
                new RegistroRequest("Ana", "López", "ana@test.com", "secreta123", null)))
                .isInstanceOf(EmailEnUsoException.class);
        verify(usuarioRepositorio, never()).save(any());
    }

    @Test
    void registrar_conContrasenaCorta_lanzaExcepcion() {
        assertThatThrownBy(() -> servicio.registrar(
                new RegistroRequest("Ana", "López", "ana@test.com", "123", null)))
                .isInstanceOf(DatosInvalidosException.class);
    }

    @Test
    void registrar_conEmailMalFormado_lanzaExcepcion() {
        assertThatThrownBy(() -> servicio.registrar(
                new RegistroRequest("Ana", "López", "no-es-un-email", "secreta123", null)))
                .isInstanceOf(DatosInvalidosException.class);
    }

    @Test
    void login_conCredencialesCorrectas_devuelveUsuarioSinContrasena() {
        when(usuarioRepositorio.findByEmail("ana@test.com"))
                .thenReturn(Optional.of(usuarioGuardado("ana@test.com", "secreta123")));

        UsuarioDTO dto = servicio.login(new LoginRequest("ana@test.com", "secreta123"));

        assertThat(dto.email()).isEqualTo("ana@test.com");
        assertThat(dto.nombre()).isEqualTo("Ana");
    }

    @Test
    void login_conContrasenaIncorrecta_lanzaExcepcion() {
        when(usuarioRepositorio.findByEmail("ana@test.com"))
                .thenReturn(Optional.of(usuarioGuardado("ana@test.com", "secreta123")));

        assertThatThrownBy(() -> servicio.login(new LoginRequest("ana@test.com", "otra")))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void login_conEmailDesconocido_lanzaLaMismaExcepcion() {
        when(usuarioRepositorio.findByEmail("nadie@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.login(new LoginRequest("nadie@test.com", "secreta123")))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void actualizar_sinNuevaContrasena_mantieneLaAnterior() {
        Usuario existente = usuarioGuardado("ana@test.com", "secreta123");
        String hashAnterior = existente.getPassword();
        when(usuarioRepositorio.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepositorio.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        servicio.actualizarUsuario(1L, new RegistroRequest("Ana María", "López", "ana@test.com", null, null));

        assertThat(existente.getPassword()).isEqualTo(hashAnterior);
        assertThat(existente.getNombre()).isEqualTo("Ana María");
    }

    @Test
    void obtenerPorId_inexistente_lanzaRecursoNoEncontrado() {
        when(usuarioRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.obtenerUsuarioPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
