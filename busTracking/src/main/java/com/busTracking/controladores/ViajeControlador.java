package com.busTracking.controladores;

import com.busTracking.entidades.Viaje;
import com.busTracking.servicios.PasajeroServicio;
import com.busTracking.servicios.ViajeServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/viajes")
public class ViajeControlador {

    private final ViajeServicio viajeServicio;
    private final PasajeroServicio pasajeroServicio;


    @Autowired
    public ViajeControlador(ViajeServicio viajeServicio, PasajeroServicio pasajeroServicio) {
        this.viajeServicio = viajeServicio;
        this.pasajeroServicio = pasajeroServicio;
    }

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarViaje(@RequestBody Map<String, Object> requestData) {
        Long pasajeroId = Long.valueOf(requestData.get("pasajeroId").toString());
        Long busId = Long.valueOf(requestData.get("busId").toString());
        Long rutaId = Long.valueOf(requestData.get("rutaId").toString());

        if (viajeServicio.tieneViajeEnProgreso(pasajeroId)) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("error", "El pasajero ya tiene un viaje en progreso");
            return new ResponseEntity<>(respuesta, HttpStatus.CONFLICT);
        }

        try {
            Viaje viaje = viajeServicio.iniciarViaje(pasajeroId, busId, rutaId);

            pasajeroServicio.incrementarNumViajes(pasajeroId);

            return new ResponseEntity<>(viaje, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("error", e.getMessage());
            return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
        } catch (NullPointerException | ClassCastException e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("error", "Formato de datos inválido: se requieren pasajeroId, busId y rutaId");
            return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{viajeId}/finalizar")
    public ResponseEntity<?> finalizarViaje(@PathVariable Long viajeId) {
        try {
            Viaje viaje = viajeServicio.finalizarViaje(viajeId);
            return ResponseEntity.ok(viaje);
        } catch (IllegalArgumentException e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("error", e.getMessage());
            return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/en-progreso")
    public ResponseEntity<List<Viaje>> obtenerViajesEnProgreso() {
        List<Viaje> viajes = viajeServicio.obtenerViajesEnProgreso();
        return ResponseEntity.ok(viajes);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Viaje> obtenerViajePorId(@PathVariable Long id) {
        Optional<Viaje> viaje = viajeServicio.obtenerViajePorId(id);
        return viaje.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/pasajero/{pasajeroId}")
    public ResponseEntity<List<Viaje>> obtenerViajesPorPasajero(@PathVariable Long pasajeroId) {
        List<Viaje> viajes = viajeServicio.obtenerViajesPorPasajero(pasajeroId);
        return ResponseEntity.ok(viajes);
    }


    @GetMapping("/pasajero/{pasajeroId}/ultimos")
    public ResponseEntity<List<Viaje>> obtenerUltimosViajesDePasajero(
            @PathVariable Long pasajeroId,
            @RequestParam(defaultValue = "5") int cantidad) {

        List<Viaje> viajes = viajeServicio.obtenerUltimosViajesDePasajero(pasajeroId, cantidad);
        return ResponseEntity.ok(viajes);
    }


    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<Viaje>> obtenerViajesPorBus(@PathVariable Long busId) {
        List<Viaje> viajes = viajeServicio.obtenerViajesPorBus(busId);
        return ResponseEntity.ok(viajes);
    }


    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<List<Viaje>> obtenerViajesPorRuta(@PathVariable Long rutaId) {
        List<Viaje> viajes = viajeServicio.obtenerViajesPorRuta(rutaId);
        return ResponseEntity.ok(viajes);
    }


    @GetMapping("/{id}/duracion")
    public ResponseEntity<?> obtenerDuracionViaje(@PathVariable Long id) {
        try {
            Duration duracion = viajeServicio.obtenerDuracionViaje(id);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("segundos", duracion.getSeconds());
            respuesta.put("minutos", duracion.toMinutes());
            respuesta.put("horas", duracion.toHours());

            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("error", e.getMessage());
            return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/rango")
    public ResponseEntity<List<Viaje>> obtenerViajesEntreFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        List<Viaje> viajes = viajeServicio.obtenerViajesEntreFechas(inicio, fin);
        return ResponseEntity.ok(viajes);
    }


    @GetMapping
    public ResponseEntity<Page<Viaje>> obtenerViajesPaginados(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano) {

        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by("inicio").descending());
        Page<Viaje> viajes = viajeServicio.obtenerViajesPaginados(pageable);
        return ResponseEntity.ok(viajes);
    }


    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        long totalViajes = viajeServicio.contarViajes();
        List<Viaje> viajesEnProgreso = viajeServicio.obtenerViajesEnProgreso();
        List<Viaje> viajesCompletados = viajeServicio.obtenerViajesCompletados();

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalViajes", totalViajes);
        estadisticas.put("viajesEnProgreso", viajesEnProgreso.size());
        estadisticas.put("viajesCompletados", viajesCompletados.size());

        return ResponseEntity.ok(estadisticas);
    }


    @GetMapping("/pasajero/{pasajeroId}/en-progreso")
    public ResponseEntity<Boolean> tieneViajeEnProgreso(@PathVariable Long pasajeroId) {
        boolean tieneViajeEnProgreso = viajeServicio.tieneViajeEnProgreso(pasajeroId);
        return ResponseEntity.ok(tieneViajeEnProgreso);
    }
}