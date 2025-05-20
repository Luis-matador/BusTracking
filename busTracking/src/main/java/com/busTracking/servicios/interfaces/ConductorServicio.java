package com.busTracking.servicios.interfaces;

import com.busTracking.modelo.entidades.Conductor;

import java.util.List;

public interface ConductorServicio {

    Conductor crearConductor(Conductor conductor);

    Conductor obtenerConductorPorId(Long id);

    List<Conductor> obtenerTodosLosConductores();

    Conductor actualizarConductor(Long id, Conductor conductor);

    void eliminarConductor(Long id);

    List<Conductor> buscarConductoresPorNombre(String nombre);

    List<Conductor> buscarConductoresPorApellido(String apellido);

    List<Conductor> buscarConductorPorDni(String dni);

    boolean existeConductorPorDni(String dni);

    List<Conductor> buscarPorNombreOApeParcial(String cadena);

    Conductor buscarConductorPorBusId(Long busId);
}
