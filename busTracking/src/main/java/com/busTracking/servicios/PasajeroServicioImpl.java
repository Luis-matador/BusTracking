package com.busTracking.servicios;

import com.busTracking.entidades.Pasajero;
import com.busTracking.repositorios.PasajeroRepositorio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PasajeroServicioImpl implements PasajeroServicio {

    private final PasajeroRepositorio pasajeroRepositorio;

    @Autowired
    public PasajeroServicioImpl(PasajeroRepositorio pasajeroRepositorio) {
        this.pasajeroRepositorio = pasajeroRepositorio;
    }

    @Override
    @Transactional
    public Pasajero guardarPasajero(Pasajero pasajero) {
        validarDatosPasajero(pasajero);

        if (pasajero.getId() == null && pasajero.getNumViajes() == null) {
            pasajero.setNumViajes(0);
        }

        if (pasajero.getTelefono() != null && existePasajeroConTelefono(pasajero.getTelefono())) {
            if (pasajero.getId() == null) {
                throw new IllegalArgumentException("Ya existe un pasajero con el teléfono: " + pasajero.getTelefono());
            } else {
                Pasajero existente = pasajeroRepositorio.findByTelefono(pasajero.getTelefono());
                if (!existente.getId().equals(pasajero.getId())) {
                    throw new IllegalArgumentException("Ya existe un pasajero con el teléfono: " + pasajero.getTelefono());
                }
            }
        }

        return pasajeroRepositorio.save(pasajero);
    }

    @Override
    public List<Pasajero> obtenerTodosPasajeros() {
        return pasajeroRepositorio.findAll();
    }

    @Override
    public Optional<Pasajero> obtenerPasajeroPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del pasajero no puede ser nulo");
        }
        return pasajeroRepositorio.findById(id);
    }

    @Override
    public Pasajero obtenerPasajeroObligatorioPorId(Long id) {
        return obtenerPasajeroPorId(id).orElseThrow(() -> new EntityNotFoundException("Pasajero con ID: " + id + " no encontrado"));
    }

    @Override
    @Transactional
    public Pasajero actualizarPasajero(Pasajero pasajero) {
        if (pasajero.getId() == null) {
            throw new IllegalArgumentException("El ID del pasajero no puede ser nulo para actualización");
        }

        Pasajero pasajeroExistente = pasajeroRepositorio.findById(pasajero.getId()).orElseThrow(() -> new EntityNotFoundException("Pasajero con ID: " + pasajero.getId() + " no encontrado"));

        validarDatosPasajero(pasajero);

        if (pasajero.getTelefono() != null &&
                !pasajero.getTelefono().equals(pasajeroExistente.getTelefono()) &&
                existePasajeroConTelefono(pasajero.getTelefono())) {

            Pasajero existente = pasajeroRepositorio.findByTelefono(pasajero.getTelefono());
            if (!existente.getId().equals(pasajero.getId())) {
                throw new IllegalArgumentException("Ya existe un pasajero con el teléfono: " + pasajero.getTelefono());
            }
        }

        if (pasajero.getNombre() != null) {
            pasajeroExistente.setNombre(pasajero.getNombre());
        }

        if (pasajero.getApellido() != null) {
            pasajeroExistente.setApellido(pasajero.getApellido());
        }

        if (pasajero.getTelefono() != null) {
            pasajeroExistente.setTelefono(pasajero.getTelefono());
        }

        if (pasajero.getNumViajes() != null) {
            pasajeroExistente.setNumViajes(pasajero.getNumViajes());
        }


        return pasajeroRepositorio.save(pasajeroExistente);
    }

    @Override
    @Transactional
    public void eliminarPasajero(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del pasajero no puede ser nulo");
        }

        if (!pasajeroRepositorio.existsById(id)) {
            throw new EntityNotFoundException("Pasajero con ID: " + id + " no encontrado");
        }

        pasajeroRepositorio.deleteById(id);
    }

    @Override
    public Pasajero buscarPorTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono no puede estar vacío");
        }

        return pasajeroRepositorio.findByTelefono(telefono);
    }

    @Override
    public List<Pasajero> buscarPorNumViajesMayorQue(Integer viajes) {
        if (viajes == null) {
            throw new IllegalArgumentException("El número de viajes no puede ser nulo");
        }

        return pasajeroRepositorio.findByNumViajesMayorQue(viajes);
    }

    @Override
    public List<Pasajero> buscarPorNombreOApellido(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            throw new IllegalArgumentException("El término de búsqueda no puede estar vacío");
        }

        return pasajeroRepositorio.findByNombreOrApellido(termino, termino);
    }

    @Override
    public List<Pasajero> obtenerTodosPasajerosOrdenadosPorViajes() {
        return pasajeroRepositorio.findAllOrderByNumViajesDesc();
    }

    @Override
    @Transactional
    public Pasajero incrementarNumViajes(Long pasajeroId) {
        if (pasajeroId == null) {
            throw new IllegalArgumentException("El ID del pasajero no puede ser nulo");
        }

        Pasajero pasajero = pasajeroRepositorio.findById(pasajeroId)
                .orElseThrow(() -> new EntityNotFoundException("Pasajero con ID: " + pasajeroId + " no encontrado"));

        pasajero.setNumViajes(pasajero.getNumViajes() + 1);
        return pasajeroRepositorio.save(pasajero);
    }

    @Override
    public boolean existePasajeroConTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;
        }

        return pasajeroRepositorio.findByTelefono(telefono) != null;
    }

    @Override
    public long contarPasajeros() {
        return pasajeroRepositorio.count();
    }


    private void validarDatosPasajero(Pasajero pasajero) {
        if (pasajero == null) {
            throw new IllegalArgumentException("El pasajero no puede ser nulo");
        }

        if (pasajero.getNombre() == null || pasajero.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del pasajero no puede estar vacío");
        }

        if (pasajero.getApellido() == null || pasajero.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del pasajero no puede estar vacío");
        }

        if (pasajero.getTelefono() == null || pasajero.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono del pasajero no puede estar vacío");
        }

        if (!pasajero.getTelefono().matches("\\d{9,15}")) {
            throw new IllegalArgumentException("El formato del teléfono no es válido. Debe contener entre 9 y 15 dígitos");
        }

        if (pasajero.getNumViajes() != null && pasajero.getNumViajes() < 0) {
            throw new IllegalArgumentException("El número de viajes no puede ser negativo");
        }
    }
}