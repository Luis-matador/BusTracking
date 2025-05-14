package com.busTracking.servicios;

import com.busTracking.entidades.Viaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ViajeServicio {

    Viaje guardarViaje(Viaje viaje);

    List<Viaje> obtenerTodosViajes();

    Page<Viaje> obtenerViajesPaginados(Pageable pageable);

    Optional<Viaje> obtenerViajePorId(Long id);

    Viaje obtenerViajeObligatorioPorId(Long id);

    Viaje actualizarViaje(Viaje viaje);

    void eliminarViaje(Long id);

    List<Viaje> obtenerViajesPorPasajero(Long pasajeroId);

    List<Viaje> obtenerViajesPorBus(Long busId);

    List<Viaje> obtenerViajesPorRuta(Long rutaId);

    List<Viaje> obtenerViajesEnProgreso();

    Long contarViajesPorPasajero(Long pasajeroId);

    Viaje iniciarViaje(Long pasajeroId, Long busId, Long rutaId);

    Viaje finalizarViaje(Long viajeId);

    List<Viaje> obtenerViajesEntreFechas(LocalDateTime inicio, LocalDateTime fin);

    long contarViajes();

    boolean tieneViajeEnProgreso(Long pasajeroId);

    Duration obtenerDuracionViaje(Long viajeId);

    List<Viaje> obtenerViajesCompletados();

    List<Viaje> obtenerUltimosViajesDePasajero(Long pasajeroId, int cantidad);
}