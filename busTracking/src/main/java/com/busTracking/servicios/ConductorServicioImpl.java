package com.busTracking.servicios;

import com.busTracking.entidades.Conductor;
import com.busTracking.repositorios.ConductorRepositorio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConductorServicioImpl implements ConductorServicio {

    private final ConductorRepositorio conductorRepositorio;

    public ConductorServicioImpl(ConductorRepositorio conductorRepositorio) {
        this.conductorRepositorio = conductorRepositorio;
    }

    @Override
    @Transactional
    public Conductor crearConductor(Conductor conductor) {

        if (conductor.getDni() != null && !conductor.getDni().isEmpty()) {
            if (conductorRepositorio.existsByDni(conductor.getDni())) {
                throw new IllegalArgumentException("Ya existe un conductor con el DNI: " + conductor.getDni());
            }
        }

        if (conductor.getBusAsignado() != null) {
            conductor.getBusAsignado().setConductor(conductor);
        }

        return conductorRepositorio.save(conductor);
    }

    @Override
    public Conductor obtenerConductorPorId(Long id) {
        return conductorRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conductor con id: " + id + " no encontrado"));
    }

    @Override
    public List<Conductor> obtenerTodosLosConductores() {
        return conductorRepositorio.findAll();
    }

    @Override
    @Transactional
    public Conductor actualizarConductor(Long id, Conductor conductor) {
        Conductor conductorExistente = conductorRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Conductor con ID: " + id + " no encontrado"));

        if (conductor.getDni() != null && !conductor.getDni().equals(conductorExistente.getDni())) {
            if (conductorRepositorio.existsByDni(conductor.getDni())) {
                throw new IllegalArgumentException("Ya existe un conductor con el DNI: " + conductor.getDni());
            }
        }

        conductorExistente.setNombre(conductor.getNombre());
        conductorExistente.setApellido(conductor.getApellido());
        conductorExistente.setDni(conductor.getDni());
        conductorExistente.setTel(conductor.getTel());

        if (conductorExistente.getBusAsignado() != null) {

            Long busExistenteId = conductorExistente.getBusAsignado().getId();
            Long nuevoId = conductor.getBusAsignado() != null ? conductor.getBusAsignado().getId() : null;

            if (nuevoId == null || !busExistenteId.equals(nuevoId)) {

                conductorExistente.getBusAsignado().setConductor(null);
            }
        }

        if (conductor.getBusAsignado() != null) {

            conductor.getBusAsignado().setConductor(conductorExistente);
        }

        conductorExistente.setBusAsignado(conductor.getBusAsignado());

        return conductorRepositorio.save(conductorExistente);
    }


    @Override
    @Transactional
    public void eliminarConductor(Long id) {
        if (conductorRepositorio.existsById(id)) {
            Conductor conductor = conductorRepositorio.findById(id).get();

            if (conductor.getBusAsignado() != null) {
                conductor.getBusAsignado().setConductor(null);
                conductor.setBusAsignado(null);
            }

            conductorRepositorio.deleteById(id);
        } else {
            throw new EntityNotFoundException("Conductor con id: " + id + " no encontrado");
        }
    }

    @Override
    public List<Conductor> buscarConductoresPorNombre(String nombre) {
        return conductorRepositorio.findByNombre(nombre);
    }

    @Override
    public List<Conductor> buscarConductoresPorApellido(String apellido) {
        return conductorRepositorio.findByApellido(apellido);
    }

    @Override
    public List<Conductor> buscarConductorPorDni(String dni) {
        return conductorRepositorio.findByDni(dni);
    }

    @Override
    public boolean existeConductorPorDni(String dni) {
        return conductorRepositorio.existsByDni(dni);
    }

    @Override
    public List<Conductor> buscarPorNombreOApeParcial(String cadena) {
        return conductorRepositorio.buscarPorNombreOApeParcial(cadena);
    }

    @Override
    public Conductor buscarConductorPorBusId(Long busId) {
        return conductorRepositorio.findConductorByBus(busId).orElseThrow(() -> new EntityNotFoundException("Conductor asociado al busId: " + busId + " no encontrado"));
    }
}