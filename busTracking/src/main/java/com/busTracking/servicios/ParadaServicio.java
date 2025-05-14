package com.busTracking.servicios;

import com.busTracking.entidades.Parada;

import java.util.List;

public interface ParadaServicio {

    Parada crearParada(Parada parada);

    Parada obtenerParadaPorId(Long id);

    List<Parada> obtenerTodasLasParadas();

    List<Parada> obtenerParadasPorRuta(Long rutaId);

    List<Parada> obtenerParadasPorRutaOrdenadas(Long rutaId);

    List<Parada> buscarParadasPorNombre(String nombre);

    List<Parada> buscarParadasCercanas(Double latitud, Double longitud, Double margen);

    void eliminarParada(Long id);

    Parada actualizarParada(Long id, Parada parada);

    void reordenarParadasEnRuta(Long rutaId, List<Long> nuevosOrdenes);
}