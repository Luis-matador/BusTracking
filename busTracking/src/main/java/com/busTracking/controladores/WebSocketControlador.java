package com.busTracking.controladores;

import com.busTracking.modelo.entidades.GPSData;
import com.busTracking.servicios.interfaces.GPSDataServicio;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class WebSocketControlador {

    private final GPSDataServicio gpsDataServicio;

    public WebSocketControlador(GPSDataServicio gpsDataServicio) {
        this.gpsDataServicio = gpsDataServicio;
    }

    @MessageMapping("/solicitar-posiciones")
    @SendTo("/topic/posiciones-buses")
    public List<GPSData> solicitarPosiciones() {
        return gpsDataServicio.obtenerUltimoGPSDataParaTodosLosBuses();
    }

    @MessageMapping("/solicitar-posicion-bus/{busId}")
    @SendTo("/topic/posicion-bus/{busId}")
    public GPSData solicitarPosicionBus(@DestinationVariable Long busId) {
        return gpsDataServicio.obtenerUltimoGPSDataPorBus(busId);
    }
}