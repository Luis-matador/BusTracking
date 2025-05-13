package com.busTracking.Servicios;

import com.busTracking.entidades.Conductor;
import com.busTracking.repositorios.ConductorRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConductorServicioImpl implements ConductorServicio {

    private final ConductorRepositorio conductorRepositorio;

    public ConductorServicioImpl(ConductorRepositorio conductorRepositorio) {
        this.conductorRepositorio = conductorRepositorio;
    }


    @Override
    public Conductor crearConductor(Conductor conductor) {
        return conductorRepositorio.save(conductor);
    }

    @Override
    public Conductor obtenerConductorPorId(Long id) {
        return conductorRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Conductor con id: " + id + " no encontrado"));
    }

    @Override
    public List<Conductor> obtenerTodosLosConductores() {
        return conductorRepositorio.findAll();
    }

    @Override
    public Conductor actualizarConductor(Long id, Conductor conductor) {
        Conductor conductorExistente = conductorRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Conductor con ID: " + id+ " no encontrado"));

        conductorExistente.setNombre(conductor.getNombre());
        conductorExistente.setApellido(conductor.getApellido());
        conductorExistente.setDni(conductor.getDni());
        conductorExistente.setTel(conductor.getTel());
        conductorExistente.setBusAsignado(conductor.getBusAsignado());

        return conductorRepositorio.save(conductorExistente);
    }

    @Override
    public void eliminarConductor(Long id) {
        if(conductorRepositorio.existsById(id)){
            conductorRepositorio.deleteById(id);
        }else{
            throw new RuntimeException("Conductor con id: " + id + " no encontrado");
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
        return conductorRepositorio.findConductorByBus(busId).orElseThrow(() -> new RuntimeException("Conductor asociado al busId: " + busId + " no encontrado"));
    }
}
