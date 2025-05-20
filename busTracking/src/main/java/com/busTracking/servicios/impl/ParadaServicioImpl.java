package com.busTracking.servicios.impl;

import com.busTracking.modelo.entidades.Parada;
import com.busTracking.repositorios.ParadaRepositorio;
import com.busTracking.repositorios.RutaRepositorio;
import com.busTracking.servicios.interfaces.ParadaServicio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParadaServicioImpl implements ParadaServicio {

    private final ParadaRepositorio paradaRepositorio;
    private final RutaRepositorio rutaRepositorio;

    @Autowired
    public ParadaServicioImpl(ParadaRepositorio paradaRepositorio, RutaRepositorio rutaRepositorio) {
        this.paradaRepositorio = paradaRepositorio;
        this.rutaRepositorio = rutaRepositorio;
    }

    @Override
    @Transactional
    public Parada crearParada(Parada parada) {
        validarCamposParada(parada);

        if (parada.getRutaAsociada() != null) {
            Long rutaId = parada.getRutaAsociada().getId();
            if (rutaId != null) {
                if (!rutaRepositorio.existsById(rutaId)) {
                    throw new EntityNotFoundException("No se encontró la ruta con ID: " + rutaId);
                }
            } else {
                throw new IllegalArgumentException("La ruta proporcionada no tiene un ID válido");
            }
        }

        return paradaRepositorio.save(parada);
    }

    @Override
    public Parada obtenerParadaPorId(Long id) {
        return paradaRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parada con id " + id + " no encontrada"));
    }

    @Override
    public List<Parada> obtenerTodasLasParadas() {
        return paradaRepositorio.findAll();
    }

    @Override
    public List<Parada> obtenerParadasPorRuta(Long rutaId) {
        // Verificar que la ruta existe
        if (!rutaRepositorio.existsById(rutaId)) {
            throw new EntityNotFoundException("No se encontró la ruta con ID: " + rutaId);
        }

        return paradaRepositorio.findByRutaAsociadaId(rutaId);
    }

    @Override
    public List<Parada> obtenerParadasPorRutaOrdenadas(Long rutaId) {
        if (!rutaRepositorio.existsById(rutaId)) {
            throw new EntityNotFoundException("No se encontró la ruta con ID: " + rutaId);
        }

        return paradaRepositorio.findByRutaAsociadaIdOrderByOrdenAsc(rutaId);
    }

    @Override
    public List<Parada> buscarParadasPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de búsqueda no puede estar vacío");
        }

        return paradaRepositorio.findByNombre(nombre);
    }

    @Override
    public List<Parada> buscarParadasCercanas(Double latitud, Double longitud, Double margen) {
        if (latitud == null || longitud == null || margen == null) {
            throw new IllegalArgumentException("Los parámetros de búsqueda no pueden ser nulos");
        }

        if (latitud < -90 || latitud > 90) {
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90 grados");
        }

        if (longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180 grados");
        }

        if (margen <= 0) {
            throw new IllegalArgumentException("El margen de búsqueda debe ser mayor que cero");
        }

        return paradaRepositorio.findParadasCercanas(latitud, longitud, margen);
    }

    @Override
    @Transactional
    public void eliminarParada(Long id) {
        if (!paradaRepositorio.existsById(id)) {
            throw new EntityNotFoundException("Parada con id " + id + " no encontrada");
        }
        paradaRepositorio.deleteById(id);
    }

    @Transactional
    @Override
    public Parada actualizarParada(Long id, Parada parada) {
        // Verificar que la parada existe
        Parada paradaExistente = paradaRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parada con id " + id + " no encontrada"));

        if (parada.getNombre() != null) {
            paradaExistente.setNombre(parada.getNombre());
        }

        if (parada.getLatitud() != null) {
            paradaExistente.setLatitud(parada.getLatitud());
        }

        if (parada.getLongitud() != null) {
            paradaExistente.setLongitud(parada.getLongitud());
        }

        if (parada.getOrden() != null) {
            paradaExistente.setOrden(parada.getOrden());
        }

        if (parada.getRutaAsociada() != null) {
            Long rutaId = parada.getRutaAsociada().getId();
            if (rutaId != null) {
                if (!rutaRepositorio.existsById(rutaId)) {
                    throw new EntityNotFoundException("No se encontró la ruta con ID: " + rutaId);
                }
                paradaExistente.setRutaAsociada(parada.getRutaAsociada());
            } else {
                throw new IllegalArgumentException("La ruta proporcionada no tiene un ID válido");
            }
        }

        validarCamposParada(paradaExistente);

        return paradaRepositorio.save(paradaExistente);
    }

    @Transactional
    @Override
    public void reordenarParadasEnRuta(Long rutaId, List<Long> nuevosOrdenes) {
        if (!rutaRepositorio.existsById(rutaId)) {
            throw new EntityNotFoundException("No se encontró la ruta con ID: " + rutaId);
        }

        List<Parada> paradas = paradaRepositorio.findByRutaAsociadaId(rutaId);

        if (paradas.size() != nuevosOrdenes.size()) {
            throw new IllegalArgumentException("La cantidad de paradas no coincide con la cantidad de órdenes proporcionados");
        }

        java.util.Map<Long, Parada> paradaPorId = new java.util.HashMap<>();
        for (Parada parada : paradas) {
            paradaPorId.put(parada.getId(), parada);
        }

        for (int i = 0; i < nuevosOrdenes.size(); i++) {
            Long paradaId = nuevosOrdenes.get(i);
            Parada parada = paradaPorId.get(paradaId);

            if (parada == null) {
                throw new EntityNotFoundException("Parada con id " + paradaId + " no encontrada en la ruta " + rutaId);
            }

            parada.setOrden(i + 1);
        }

        paradaRepositorio.saveAll(paradas);
    }


    private void validarCamposParada(Parada parada) {
        if (parada.getNombre() == null || parada.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la parada no puede estar vacío");
        }

        if (parada.getLatitud() == null) {
            throw new IllegalArgumentException("La latitud de la parada no puede ser nula");
        }

        if (parada.getLatitud() < -90 || parada.getLatitud() > 90) {
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90 grados");
        }

        if (parada.getLongitud() == null) {
            throw new IllegalArgumentException("La longitud de la parada no puede ser nula");
        }

        if (parada.getLongitud() < -180 || parada.getLongitud() > 180) {
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180 grados");
        }
    }
}