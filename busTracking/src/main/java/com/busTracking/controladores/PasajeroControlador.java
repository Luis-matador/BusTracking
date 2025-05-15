package com.busTracking.controladores;

import com.busTracking.entidades.Pasajero;
import com.busTracking.servicios.PasajeroServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/pasajeros")
public class PasajeroControlador {

    private final PasajeroServicio pasajeroServicio;

    @Autowired
    public PasajeroControlador(PasajeroServicio pasajeroServicio) {
        this.pasajeroServicio = pasajeroServicio;
    }

    @PostMapping
    public ResponseEntity<Pasajero> registrarPasajero(@RequestBody Pasajero pasajero) {
        if (pasajero.getTelefono() != null && pasajeroServicio.existePasajeroConTelefono(pasajero.getTelefono())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if (pasajero.getNumViajes() == null) {
            pasajero.setNumViajes(0);
        }

        Pasajero nuevoPasajero = pasajeroServicio.guardarPasajero(pasajero);
        return new ResponseEntity<>(nuevoPasajero, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Pasajero> obtenerPasajeroPorId(@PathVariable Long id) {
        Optional<Pasajero> pasajero = pasajeroServicio.obtenerPasajeroPorId(id);
        return pasajero.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<List<Pasajero>> obtenerTodosPasajeros() {
        List<Pasajero> pasajeros = pasajeroServicio.obtenerTodosPasajeros();
        return ResponseEntity.ok(pasajeros);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Pasajero> actualizarPasajero(@PathVariable Long id, @RequestBody Pasajero pasajero) {
        Optional<Pasajero> pasajeroExistente = pasajeroServicio.obtenerPasajeroPorId(id);
        if (pasajeroExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (pasajero.getTelefono() != null) {
            Pasajero pasajeroConTelefono = pasajeroServicio.buscarPorTelefono(pasajero.getTelefono());
            if (pasajeroConTelefono != null && !pasajeroConTelefono.getId().equals(id)) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }

        pasajero.setId(id);
        Pasajero pasajeroActualizado = pasajeroServicio.actualizarPasajero(pasajero);
        return ResponseEntity.ok(pasajeroActualizado);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPasajero(@PathVariable Long id) {
        if (pasajeroServicio.obtenerPasajeroPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        pasajeroServicio.eliminarPasajero(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/telefono/{telefono}")
    public ResponseEntity<Pasajero> buscarPorTelefono(@PathVariable String telefono) {
        Pasajero pasajero = pasajeroServicio.buscarPorTelefono(telefono);
        if (pasajero != null) {
            return ResponseEntity.ok(pasajero);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/buscar")
    public ResponseEntity<List<Pasajero>> buscarPorNombreOApellido(@RequestParam String termino) {
        List<Pasajero> pasajeros = pasajeroServicio.buscarPorNombreOApellido(termino);
        return ResponseEntity.ok(pasajeros);
    }


    @GetMapping("/viajes-mayor-que/{viajes}")
    public ResponseEntity<List<Pasajero>> buscarPorNumViajesMayorQue(@PathVariable Integer viajes) {
        List<Pasajero> pasajeros = pasajeroServicio.buscarPorNumViajesMayorQue(viajes);
        return ResponseEntity.ok(pasajeros);
    }


    @GetMapping("/ordenados-por-viajes")
    public ResponseEntity<List<Pasajero>> obtenerTodosPasajerosOrdenadosPorViajes() {
        List<Pasajero> pasajeros = pasajeroServicio.obtenerTodosPasajerosOrdenadosPorViajes();
        return ResponseEntity.ok(pasajeros);
    }


    @PostMapping("/{id}/incrementar-viajes")
    public ResponseEntity<Pasajero> incrementarNumViajes(@PathVariable Long id) {
        if (pasajeroServicio.obtenerPasajeroPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pasajero pasajeroActualizado = pasajeroServicio.incrementarNumViajes(id);
        return ResponseEntity.ok(pasajeroActualizado);
    }

    @GetMapping("/existe/telefono/{telefono}")
    public ResponseEntity<Boolean> existePasajeroConTelefono(@PathVariable String telefono) {
        boolean existe = pasajeroServicio.existePasajeroConTelefono(telefono);
        return ResponseEntity.ok(existe);
    }
}


