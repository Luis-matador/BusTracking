package com.busTracking.servicios;

import com.busTracking.entidades.Bus;
import com.busTracking.entidades.Parada;
import com.busTracking.entidades.Ruta;
import com.busTracking.repositorios.BusRepositorio;
import com.busTracking.repositorios.ParadaRepositorio;
import com.busTracking.repositorios.RutaRepositorio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RutaServicioImpl implements RutaServicio {

    private final RutaRepositorio rutaRepositorio;
    private final ParadaRepositorio paradaRepositorio;
    private final BusRepositorio busRepositorio;

    @Autowired
    public RutaServicioImpl(RutaRepositorio rutaRepositorio, ParadaRepositorio paradaRepositorio, BusRepositorio busRepositorio) {
        this.rutaRepositorio = rutaRepositorio;
        this.paradaRepositorio = paradaRepositorio;
        this.busRepositorio = busRepositorio;
    }

    @Override
    @Transactional
    public Ruta guardarRuta(Ruta ruta) {
        validarCamposRuta(ruta);

        if (ruta.getNombre() != null && existeRutaConNombre(ruta.getNombre())) {
            if (ruta.getId() == null) {
                throw new IllegalArgumentException("Ya existe una ruta con el nombre: " + ruta.getNombre());
            } else {
                Ruta existente = rutaRepositorio.findByNombre(ruta.getNombre());
                if (!existente.getId().equals(ruta.getId())) {
                    throw new IllegalArgumentException("Ya existe otra ruta con el nombre: " + ruta.getNombre());
                }
            }
        }

        return rutaRepositorio.save(ruta);
    }

    @Override
    public List<Ruta> obtenerTodasRutas() {
        return rutaRepositorio.findAll();
    }

    @Override
    public Optional<Ruta> obtenerRutaPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la ruta no puede ser nulo");
        }
        return rutaRepositorio.findById(id);
    }

    @Override
    public Ruta obtenerRutaObligatoriaPorId(Long id) {
        return obtenerRutaPorId(id).orElseThrow(() -> new EntityNotFoundException("Ruta con ID: " + id + " no encontrada"));
    }

    @Override
    @Transactional
    public Ruta actualizarRuta(Ruta ruta) {
        if (ruta.getId() == null) {
            throw new IllegalArgumentException("El ID de la ruta no puede ser nulo para actualización");
        }

        Ruta rutaExistente = rutaRepositorio.findById(ruta.getId()).orElseThrow(() -> new EntityNotFoundException("Ruta con ID: " + ruta.getId() + " no encontrada"));

        validarCamposRuta(ruta);

        if (ruta.getNombre() != null && !ruta.getNombre().equals(rutaExistente.getNombre()) && existeRutaConNombre(ruta.getNombre())) {

            Ruta existente = rutaRepositorio.findByNombre(ruta.getNombre());
            if (!existente.getId().equals(ruta.getId())) {
                throw new IllegalArgumentException("Ya existe otra ruta con el nombre: " + ruta.getNombre());
            }
        }

        if (ruta.getNombre() != null) {
            rutaExistente.setNombre(ruta.getNombre());
        }

        if (ruta.getInfo() != null) {
            rutaExistente.setInfo(ruta.getInfo());
        }

        return rutaRepositorio.save(rutaExistente);
    }

    @Override
    @Transactional
    public void eliminarRuta(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la ruta no puede ser nulo");
        }

        if (!rutaRepositorio.existsById(id)) {
            throw new EntityNotFoundException("Ruta con ID: " + id + " no encontrada");
        }

        rutaRepositorio.deleteById(id);
    }

    @Override
    public Ruta buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        Ruta ruta = rutaRepositorio.findByNombre(nombre);
        if (ruta == null) {
            throw new EntityNotFoundException("No se encontró ninguna ruta con el nombre: " + nombre);
        }

        return ruta;
    }

    @Override
    public List<Ruta> buscarPorNombreSimilar(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El término de búsqueda no puede estar vacío");
        }

        return rutaRepositorio.findByNombreLike(nombre);
    }

    @Override
    public boolean existeRutaConNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        return rutaRepositorio.existsByNombre(nombre);
    }

    @Override
    public List<Ruta> obtenerInformacionBasica() {
        return rutaRepositorio.findAllBasicInfo();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ruta> obtenerRutasConParadas() {
        return rutaRepositorio.findAll().stream().filter(ruta -> ruta.getParadas() != null && !ruta.getParadas().isEmpty()).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ruta> obtenerRutasConBuses() {
        return rutaRepositorio.findAll().stream().filter(ruta -> ruta.getBuses() != null && !ruta.getBuses().isEmpty()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Ruta agregarParadasARuta(Long rutaId, List<Long> paradaIds) {
        if (rutaId == null) {
            throw new IllegalArgumentException("El ID de la ruta no puede ser nulo");
        }

        if (paradaIds == null || paradaIds.isEmpty()) {
            throw new IllegalArgumentException("La lista de paradas no puede estar vacía");
        }

        Ruta ruta = obtenerRutaObligatoriaPorId(rutaId);

        List<Parada> paradasExistentes = new ArrayList<>();
        for (Long paradaId : paradaIds) {
            Parada parada = paradaRepositorio.findById(paradaId).orElseThrow(() -> new EntityNotFoundException("Parada con ID: " + paradaId + " no encontrada"));
            paradasExistentes.add(parada);
        }

        for (int i = 0; i < paradasExistentes.size(); i++) {
            Parada parada = paradasExistentes.get(i);
            parada.setOrden(i + 1); // Orden comienza en 1
            parada.setRutaAsociada(ruta);
        }

        paradaRepositorio.saveAll(paradasExistentes);

        if (ruta.getParadas() == null) {
            ruta.setParadas(paradasExistentes);
        } else {
            ruta.getParadas().removeIf(parada -> !paradaIds.contains(parada.getId()));

            for (Parada parada : paradasExistentes) {
                if (!ruta.getParadas().contains(parada)) {
                    ruta.getParadas().add(parada);
                }
            }
        }

        return rutaRepositorio.save(ruta);
    }

    @Override
    @Transactional
    public Ruta asignarBusesARuta(Long rutaId, List<Long> busIds) {
        if (rutaId == null) {
            throw new IllegalArgumentException("El ID de la ruta no puede ser nulo");
        }

        if (busIds == null || busIds.isEmpty()) {
            throw new IllegalArgumentException("La lista de buses no puede estar vacía");
        }

        Ruta ruta = obtenerRutaObligatoriaPorId(rutaId);

        List<Bus> busesExistentes = new ArrayList<>();
        for (Long busId : busIds) {
            Bus bus = busRepositorio.findById(busId).orElseThrow(() -> new EntityNotFoundException("Bus con ID: " + busId + " no encontrado"));

            if (bus.getRuta() != null && !bus.getRuta().getId().equals(rutaId)) {
                throw new IllegalArgumentException(
                        "El bus con ID: " + busId + " ya está asignado a la ruta con ID: " + bus.getRuta().getId());
            }

            busesExistentes.add(bus);
        }

        for (Bus bus : busesExistentes) {
            bus.setRuta(ruta);
        }

        busRepositorio.saveAll(busesExistentes);

        if (ruta.getBuses() == null) {
            ruta.setBuses(busesExistentes);
        } else {
            for (Bus busAnterior : ruta.getBuses()) {
                if (!busIds.contains(busAnterior.getId())) {
                    busAnterior.setRuta(null);
                    busRepositorio.save(busAnterior);
                }
            }

            ruta.getBuses().clear();
            ruta.getBuses().addAll(busesExistentes);
        }

        return rutaRepositorio.save(ruta);
    }


    private void validarCamposRuta(Ruta ruta) {
        if (ruta == null) {
            throw new IllegalArgumentException("La ruta no puede ser nula");
        }

        if (ruta.getNombre() == null || ruta.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la ruta no puede estar vacío");
        }

        if (ruta.getNombre().length() > 100) {
            throw new IllegalArgumentException("El nombre de la ruta no puede exceder los 100 caracteres");
        }
    }
}