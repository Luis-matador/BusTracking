package com.busTracking.controladores;

import com.busTracking.modelo.entidades.Bus;
import com.busTracking.modelo.entidades.Parada;
import com.busTracking.modelo.entidades.Ruta;
import com.busTracking.servicios.interfaces.RutaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/rutas")
public class RutaControlador {

    private final RutaServicio rutaServicio;


    @Autowired
    public RutaControlador(RutaServicio rutaServicio) {
        this.rutaServicio = rutaServicio;
    }


    @PostMapping
    public ResponseEntity<Ruta> crearRuta(@RequestBody Ruta ruta) {
        if (ruta.getNombre() != null && rutaServicio.existeRutaConNombre(ruta.getNombre())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict
        }

        Ruta nuevaRuta = rutaServicio.guardarRuta(ruta);
        return new ResponseEntity<>(nuevaRuta, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Ruta> obtenerRutaPorId(@PathVariable Long id) {
        Optional<Ruta> ruta = rutaServicio.obtenerRutaPorId(id);

        return ruta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<List<Ruta>> obtenerTodasRutas() {
        List<Ruta> rutas = rutaServicio.obtenerTodasRutas();
        return ResponseEntity.ok(rutas);
    }


    @GetMapping("/basicas")
    public ResponseEntity<List<Ruta>> obtenerInformacionBasica() {
        List<Ruta> rutas = rutaServicio.obtenerInformacionBasica();
        return ResponseEntity.ok(rutas);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Ruta> actualizarRuta(@PathVariable Long id, @RequestBody Ruta ruta) {
        Optional<Ruta> rutaExistente = rutaServicio.obtenerRutaPorId(id);
        if (rutaExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (ruta.getNombre() != null) {
            Ruta rutaConNombre = rutaServicio.buscarPorNombre(ruta.getNombre());
            if (rutaConNombre != null && !rutaConNombre.getId().equals(id)) {
                return new ResponseEntity<>(HttpStatus.CONFLICT); // 409 Conflict
            }
        }

        ruta.setId(id);
        Ruta rutaActualizada = rutaServicio.actualizarRuta(ruta);
        return ResponseEntity.ok(rutaActualizada);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRuta(@PathVariable Long id) {
        if (rutaServicio.obtenerRutaPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        rutaServicio.eliminarRuta(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Ruta> buscarPorNombre(@PathVariable String nombre) {
        Ruta ruta = rutaServicio.buscarPorNombre(nombre);
        if (ruta != null) {
            return ResponseEntity.ok(ruta);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/buscar")
    public ResponseEntity<List<Ruta>> buscarPorNombreSimilar(@RequestParam String nombre) {
        List<Ruta> rutas = rutaServicio.buscarPorNombreSimilar(nombre);
        return ResponseEntity.ok(rutas);
    }


    @GetMapping("/existe/nombre/{nombre}")
    public ResponseEntity<Boolean> existeRutaConNombre(@PathVariable String nombre) {
        boolean existe = rutaServicio.existeRutaConNombre(nombre);
        return ResponseEntity.ok(existe);
    }


    @GetMapping("/con-paradas")
    public ResponseEntity<List<Ruta>> obtenerRutasConParadas() {
        List<Ruta> rutas = rutaServicio.obtenerRutasConParadas();
        return ResponseEntity.ok(rutas);
    }


    @GetMapping("/con-buses")
    public ResponseEntity<List<Ruta>> obtenerRutasConBuses() {
        List<Ruta> rutas = rutaServicio.obtenerRutasConBuses();
        return ResponseEntity.ok(rutas);
    }


    @PostMapping("/{id}/paradas")
    public ResponseEntity<Ruta> agregarParadasARuta(@PathVariable Long id, @RequestBody List<Long> paradaIds) {
        try {
            Ruta ruta = rutaServicio.agregarParadasARuta(id, paradaIds);
            return ResponseEntity.ok(ruta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/{id}/buses")
    public ResponseEntity<Ruta> asignarBusesARuta(@PathVariable Long id, @RequestBody List<Long> busIds) {
        try {
            Ruta ruta = rutaServicio.asignarBusesARuta(id, busIds);
            return ResponseEntity.ok(ruta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/paradas")
    public ResponseEntity<List<Parada>> obtenerParadasDeRuta(@PathVariable Long id) {
        Optional<Ruta> rutaOptional = rutaServicio.obtenerRutaPorId(id);
        if (rutaOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Parada> paradas = rutaOptional.get().getParadas();
        return ResponseEntity.ok(paradas);
    }


    @GetMapping("/{id}/buses")
    public ResponseEntity<List<Bus>> obtenerBusesDeRuta(@PathVariable Long id) {
        Optional<Ruta> rutaOptional = rutaServicio.obtenerRutaPorId(id);
        if (rutaOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Bus> buses = rutaOptional.get().getBuses();
        return ResponseEntity.ok(buses);
    }
}