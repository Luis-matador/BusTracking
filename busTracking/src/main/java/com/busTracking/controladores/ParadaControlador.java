package com.busTracking.controladores;

import com.busTracking.entidades.Parada;
import com.busTracking.servicios.ParadaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/paradas")
public class ParadaControlador {

    private final ParadaServicio paradaServicio;

    @Autowired
    public ParadaControlador(ParadaServicio paradaServicio) {
        this.paradaServicio = paradaServicio;
    }


    @PostMapping
    public ResponseEntity<Parada> crearParada(@RequestBody Parada parada) {
        Parada nuevaParada = paradaServicio.crearParada(parada);
        return new ResponseEntity<>(nuevaParada, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Parada> obtenerParadaPorId(@PathVariable Long id) {
        Parada parada = paradaServicio.obtenerParadaPorId(id);
        if (parada != null) {
            return ResponseEntity.ok(parada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping
    public ResponseEntity<List<Parada>> obtenerTodasLasParadas() {
        List<Parada> paradas = paradaServicio.obtenerTodasLasParadas();
        return ResponseEntity.ok(paradas);
    }


    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<List<Parada>> obtenerParadasPorRuta(@PathVariable Long rutaId) {
        List<Parada> paradas = paradaServicio.obtenerParadasPorRuta(rutaId);
        return ResponseEntity.ok(paradas);
    }


    @GetMapping("/ruta/{rutaId}/ordenadas")
    public ResponseEntity<List<Parada>> obtenerParadasPorRutaOrdenadas(@PathVariable Long rutaId) {
        List<Parada> paradasOrdenadas = paradaServicio.obtenerParadasPorRutaOrdenadas(rutaId);
        return ResponseEntity.ok(paradasOrdenadas);
    }


    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<Parada>> buscarParadasPorNombre(@PathVariable String nombre) {
        List<Parada> paradas = paradaServicio.buscarParadasPorNombre(nombre);
        return ResponseEntity.ok(paradas);
    }


    @GetMapping("/cercanas")
    public ResponseEntity<List<Parada>> buscarParadasCercanas(@RequestParam Double latitud, @RequestParam Double longitud, @RequestParam(defaultValue = "0.01") Double margen) {

        List<Parada> paradasCercanas = paradaServicio.buscarParadasCercanas(latitud, longitud, margen);
        return ResponseEntity.ok(paradasCercanas);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Parada> actualizarParada(@PathVariable Long id, @RequestBody Parada parada) {
        Parada paradaActualizada = paradaServicio.actualizarParada(id, parada);
        if (paradaActualizada != null) {
            return ResponseEntity.ok(paradaActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarParada(@PathVariable Long id) {
        Parada parada = paradaServicio.obtenerParadaPorId(id);
        if (parada == null) {
            return ResponseEntity.notFound().build();
        }
        paradaServicio.eliminarParada(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/ruta/{rutaId}/reordenar")
    public ResponseEntity<Void> reordenarParadasEnRuta(@PathVariable Long rutaId, @RequestBody List<Long> nuevosOrdenes) {

        paradaServicio.reordenarParadasEnRuta(rutaId, nuevosOrdenes);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/ruta/{rutaId}/batch")
    public ResponseEntity<List<Parada>> crearParadasEnLote(@PathVariable Long rutaId, @RequestBody List<Parada> paradas) {

        for (int i = 0; i < paradas.size(); i++) {
            Parada parada = paradas.get(i);
            parada.setOrden(i + 1);
        }

        List<Parada> paradasCreadas = paradas.stream().map(paradaServicio::crearParada).toList();

        return new ResponseEntity<>(paradasCreadas, HttpStatus.CREATED);
    }


    @GetMapping("/{paradaId}/siguiente")
    public ResponseEntity<Parada> obtenerSiguienteParada(@PathVariable Long paradaId) {
        Parada paradaActual = paradaServicio.obtenerParadaPorId(paradaId);
        if (paradaActual == null) {
            return ResponseEntity.notFound().build();
        }

        Long rutaId = paradaActual.getRutaAsociada().getId();
        Integer ordenActual = paradaActual.getOrden();

        List<Parada> paradasOrdenadas = paradaServicio.obtenerParadasPorRutaOrdenadas(rutaId);

        for (Parada parada : paradasOrdenadas) {
            if (parada.getOrden() != null && parada.getOrden() > ordenActual) {
                return ResponseEntity.ok(parada);
            }
        }

        return ResponseEntity.notFound().build();
    }
}