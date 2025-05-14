package com.busTracking.servicios;

import com.busTracking.entidades.Pasajero;

import java.util.List;
import java.util.Optional;

public interface PasajeroServicio {

    Pasajero guardarPasajero(Pasajero pasajero);

    List<Pasajero> obtenerTodosPasajeros();

    Optional<Pasajero> obtenerPasajeroPorId(Long id);

    Pasajero obtenerPasajeroObligatorioPorId(Long id);

    Pasajero actualizarPasajero(Pasajero pasajero);

    void eliminarPasajero(Long id);

    Pasajero buscarPorTelefono(String telefono);

    List<Pasajero> buscarPorNumViajesMayorQue(Integer viajes);

    List<Pasajero> buscarPorNombreOApellido(String termino);

    List<Pasajero> obtenerTodosPasajerosOrdenadosPorViajes();

    Pasajero incrementarNumViajes(Long pasajeroId);

    boolean existePasajeroConTelefono(String telefono);

    long contarPasajeros();
}