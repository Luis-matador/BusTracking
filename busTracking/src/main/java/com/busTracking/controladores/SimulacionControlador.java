package com.busTracking.controladores;

import com.busTracking.modelo.entidades.Bus;
import com.busTracking.repositorios.BusRepositorio;
import com.busTracking.servicios.interfaces.SimulacionServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simulacion")
public class SimulacionControlador {


    private SimulacionServicio simulacionServicio;

    private BusRepositorio busRepositorio;


    @PostMapping("/iniciar/{busId}")
    public ResponseEntity<?> iniciarSimulacion(@PathVariable Long busId) {
        try {
            Bus bus = busRepositorio.findById(busId).orElseThrow(() -> new RuntimeException("Bus no encontrado"));

            simulacionServicio.iniciarSimulacionParaBus(bus);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Simulación iniciada para el bus " + busId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/detener/{busId}")
    public ResponseEntity<?> detenerSimulacion(@PathVariable Long busId) {
        simulacionServicio.detenerSimulacionParaBus(busId);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Simulación detenida para el bus " + busId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/detener-todo")
    public ResponseEntity<?> detenerTodasLasSimulaciones() {
        simulacionServicio.detenerSimulacion();

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Todas las simulaciones han sido detenidas");
        return ResponseEntity.ok(response);
    }
}