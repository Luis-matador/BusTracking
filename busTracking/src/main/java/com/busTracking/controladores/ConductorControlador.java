package com.busTracking.controladores;

import com.busTracking.modelo.entidades.Conductor;
import com.busTracking.servicios.interfaces.ConductorServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conductores")
public class ConductorControlador {

    private final ConductorServicio conductorServicio;

    @Autowired
    public ConductorControlador(ConductorServicio conductorServicio) {
        this.conductorServicio = conductorServicio;
    }

    @PostMapping
    public ResponseEntity<Conductor> crearConductor(@RequestBody Conductor conductor) {
        if (conductor.getDni() != null && conductorServicio.existeConductorPorDni(conductor.getDni())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict
        }

        Conductor nuevoConductor = conductorServicio.crearConductor(conductor);
        return new ResponseEntity<>(nuevoConductor, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Conductor> obtenerConductorPorId(@PathVariable Long id) {
        Conductor conductor = conductorServicio.obtenerConductorPorId(id);
        if (conductor != null) {
            return ResponseEntity.ok(conductor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping
    public ResponseEntity<List<Conductor>> obtenerTodosLosConductores() {
        List<Conductor> conductores = conductorServicio.obtenerTodosLosConductores();
        return ResponseEntity.ok(conductores);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Conductor> actualizarConductor(@PathVariable Long id, @RequestBody Conductor conductor) {
        if (conductor.getDni() != null) {
            List<Conductor> conductoresConDni = conductorServicio.buscarConductorPorDni(conductor.getDni());
            if (!conductoresConDni.isEmpty() &&
                    conductoresConDni.stream().anyMatch(c -> !c.getId().equals(id))) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }

        Conductor conductorActualizado = conductorServicio.actualizarConductor(id, conductor);
        if (conductorActualizado != null) {
            return ResponseEntity.ok(conductorActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarConductor(@PathVariable Long id) {
        Conductor conductor = conductorServicio.obtenerConductorPorId(id);
        if (conductor == null) {
            return ResponseEntity.notFound().build();
        }
        conductorServicio.eliminarConductor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<Conductor>> buscarConductoresPorNombre(@PathVariable String nombre) {
        List<Conductor> conductores = conductorServicio.buscarConductoresPorNombre(nombre);
        return ResponseEntity.ok(conductores);
    }


    @GetMapping("/apellido/{apellido}")
    public ResponseEntity<List<Conductor>> buscarConductoresPorApellido(@PathVariable String apellido) {
        List<Conductor> conductores = conductorServicio.buscarConductoresPorApellido(apellido);
        return ResponseEntity.ok(conductores);
    }


    @GetMapping("/dni/{dni}")
    public ResponseEntity<List<Conductor>> buscarConductorPorDni(@PathVariable String dni) {
        List<Conductor> conductores = conductorServicio.buscarConductorPorDni(dni);
        return ResponseEntity.ok(conductores);
    }


    @GetMapping("/existe/dni/{dni}")
    public ResponseEntity<Boolean> existeConductorPorDni(@PathVariable String dni) {
        boolean existe = conductorServicio.existeConductorPorDni(dni);
        return ResponseEntity.ok(existe);
    }


    @GetMapping("/buscar/{cadena}")
    public ResponseEntity<List<Conductor>> buscarPorNombreOApeParcial(@PathVariable String cadena) {
        List<Conductor> conductores = conductorServicio.buscarPorNombreOApeParcial(cadena);
        return ResponseEntity.ok(conductores);
    }


    @GetMapping("/bus/{busId}")
    public ResponseEntity<Conductor> buscarConductorPorBusId(@PathVariable Long busId) {
        Conductor conductor = conductorServicio.buscarConductorPorBusId(busId);
        if (conductor != null) {
            return ResponseEntity.ok(conductor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}