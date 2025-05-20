package com.busTracking.servicios.interfaces;

import com.busTracking.modelo.entidades.Bus;

public interface SimulacionServicio {


    void iniciarSimulacionParaBus(Bus bus) throws IllegalStateException;


    void detenerSimulacionParaBus(Long busId);


    void detenerSimulacion();


}