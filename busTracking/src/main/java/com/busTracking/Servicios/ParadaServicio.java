package com.busTracking.Servicios;

import com.busTracking.entidades.Parada;

import java.util.List;

public interface ParadaServicio {

    Parada crearParada(Parada parada);

    Parada obtenerParadaPorId(Long id);

    List<Parada> obtenerTodasLasParadas();

    List<Parada> obtenerParadaPorRuta(Long rutaId);

    List<Parada> obtenerParadasPorRutaOrdenadas(Long rutaId);

    List<Parada> buscarParadasPorNombre(String nombre);

    List<Parada> buscarParadasCercanas(Double latitud, Double longitud, Double margen);

    void eliminarParada(Long id);


}
