package com.busTracking.Servicios;

import com.busTracking.entidades.Parada;
import com.busTracking.repositorios.ParadaRepositorio;

import java.util.List;

public class ParadaServicioImpl implements ParadaServicio{

    private final ParadaRepositorio paradaRepositorio;

    public ParadaServicioImpl(ParadaRepositorio paradaRepositorio) {
        this.paradaRepositorio = paradaRepositorio;
    }

    @Override
    public Parada crearParada(Parada parada) {
        return paradaRepositorio.save(parada);
    }

    @Override
    public Parada obtenerParadaPorId(Long id) {
        return paradaRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Parada con id " + id + " no encontrada"));
    }

    @Override
    public List<Parada> obtenerTodasLasParadas() {
        return paradaRepositorio.findAll();
    }

    @Override
    public List<Parada> obtenerParadaPorRuta(Long rutaId) {
        return paradaRepositorio.findByRutaAsociadaId(rutaId);
    }

    @Override
    public List<Parada> obtenerParadasPorRutaOrdenadas(Long rutaId) {
        return paradaRepositorio.findByRutaAsociadaIdOrderByOrdenAsc(rutaId);
    }

    @Override
    public List<Parada> buscarParadasPorNombre(String nombre) {
        return paradaRepositorio.findByNombre(nombre);
    }

    @Override
    public List<Parada> buscarParadasCercanas(Double latitud, Double longitud, Double margen) {
        return paradaRepositorio.findParadasCercanas(latitud, longitud, margen);
    }

    @Override
    public void eliminarParada(Long id) {
        if(paradaRepositorio.existsById(id)){
            paradaRepositorio.deleteById(id);
        }else{
            throw new RuntimeException("Parada con id " + id + " no encontrada");
        }
    }
}
