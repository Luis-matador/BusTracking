package com.busTracking.servicios;

import com.busTracking.entidades.Bus;

public interface SimulacionServicio {


    void iniciarSimulacionParaBus(Bus bus) throws IllegalStateException;


    void detenerSimulacionParaBus(Long busId);


    void detenerSimulacion();
}