package com.busTracking.servicios;

import com.busTracking.entidades.Ruta;

import java.util.List;
import java.util.Optional;

public interface RutaServicio {

    Ruta guardarRuta(Ruta ruta);

    List<Ruta> obtenerTodasRutas();

    Optional<Ruta> obtenerRutaPorId(Long id);

    Ruta obtenerRutaObligatoriaPorId(Long id);

    Ruta actualizarRuta(Ruta ruta);

    void eliminarRuta(Long id);

    Ruta buscarPorNombre(String nombre);

    List<Ruta> buscarPorNombreSimilar(String nombre);

    boolean existeRutaConNombre(String nombre);

    List<Ruta> obtenerInformacionBasica();

    List<Ruta> obtenerRutasConParadas();

    List<Ruta> obtenerRutasConBuses();

    Ruta agregarParadasARuta(Long rutaId, List<Long> paradaIds);

    Ruta asignarBusesARuta(Long rutaId, List<Long> busIds);
}