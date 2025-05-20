package com.busTracking.servicios.interfaces;
import com.busTracking.modelo.entidades.Bus;
import com.busTracking.modelo.entidades.Parada;

import java.util.List;

public interface BusServicio {

    Bus crearBus(Bus bus);

    Bus obtenerBusPorId(Long id);

    List<Bus> obtenerTodosLosBuses();

    Bus actualizarBus(Long id, Bus bus);

    void eliminarBus(Long id);

    List<Bus> obtenerBusesPorMarca(String marca);

    List<Bus> obtenerBusesPorModelo(String modelo);

    List<Bus> obtenerBusesPorMatricula(String matricula);

    List<Bus> buscarBusesPorRutaId(Long rutaId);

    List<Bus> buscarBusesPorConductorId(Long conductorId);

    Long contarBusesPorRuta(Long rutaId);

    boolean tieneBusesDisponibles(Long rutaId);

    Parada encontrarSiguienteParada(List<Parada> paradasRuta, Double latitud, Double longitud);
}
