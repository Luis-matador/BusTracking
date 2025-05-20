package com.busTracking.controladores;

import com.busTracking.modelo.entidades.Bus;
import com.busTracking.modelo.entidades.GPSData;
import com.busTracking.modelo.entidades.Parada;
import com.busTracking.servicios.interfaces.BusServicio;
import com.busTracking.servicios.interfaces.GPSDataServicio;
import com.busTracking.servicios.interfaces.ParadaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusControlador {

    private final BusServicio busServicio;
    private final ParadaServicio paradaServicio;
    private final GPSDataServicio gpsDataServicio;

    @Autowired
    public BusControlador(BusServicio busServicio, ParadaServicio paradaServicio, GPSDataServicio gpsDataServicio) {
        this.busServicio = busServicio;
        this.paradaServicio = paradaServicio;
        this.gpsDataServicio = gpsDataServicio;
    }

    @PostMapping
    public ResponseEntity<Bus> crearBus(@RequestBody Bus bus) {
        Bus nuevoBus = busServicio.crearBus(bus);
        return new ResponseEntity<>(nuevoBus, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bus> obtenerBusPorId(@PathVariable Long id) {
        Bus bus = busServicio.obtenerBusPorId(id);
        if (bus != null) {
            return ResponseEntity.ok(bus);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Bus>> obtenerTodosLosBuses() {
        List<Bus> buses = busServicio.obtenerTodosLosBuses();
        return ResponseEntity.ok(buses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bus> actualizarBus(@PathVariable Long id, @RequestBody Bus bus) {
        Bus busActualizado = busServicio.actualizarBus(id, bus);
        if (busActualizado != null) {
            return ResponseEntity.ok(busActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBus(@PathVariable Long id) {
        busServicio.eliminarBus(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<Bus>> obtenerBusesPorMarca(@PathVariable String marca) {
        List<Bus> buses = busServicio.obtenerBusesPorMarca(marca);
        return ResponseEntity.ok(buses);
    }

    @GetMapping("/modelo/{modelo}")
    public ResponseEntity<List<Bus>> obtenerBusesPorModelo(@PathVariable String modelo) {
        List<Bus> buses = busServicio.obtenerBusesPorModelo(modelo);
        return ResponseEntity.ok(buses);
    }

    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<List<Bus>> obtenerBusesPorMatricula(@PathVariable String matricula) {
        List<Bus> buses = busServicio.obtenerBusesPorMatricula(matricula);
        return ResponseEntity.ok(buses);
    }

    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<List<Bus>> buscarBusesPorRutaId(@PathVariable Long rutaId) {
        List<Bus> buses = busServicio.buscarBusesPorRutaId(rutaId);
        return ResponseEntity.ok(buses);
    }

    @GetMapping("/conductor/{conductorId}")
    public ResponseEntity<List<Bus>> buscarBusesPorConductorId(@PathVariable Long conductorId) {
        List<Bus> buses = busServicio.buscarBusesPorConductorId(conductorId);
        return ResponseEntity.ok(buses);
    }

    @GetMapping("/ruta/{rutaId}/contar")
    public ResponseEntity<Long> contarBusesPorRuta(@PathVariable Long rutaId) {
        Long cantidad = busServicio.contarBusesPorRuta(rutaId);
        return ResponseEntity.ok(cantidad);
    }

    @GetMapping("/ruta/{rutaId}/disponibilidad")
    public ResponseEntity<Boolean> verificarDisponibilidadBuses(@PathVariable Long rutaId) {
        boolean hayDisponibles = busServicio.tieneBusesDisponibles(rutaId);
        return ResponseEntity.ok(hayDisponibles);
    }

    @GetMapping("/{busId}/destino")
    public ResponseEntity<Parada> obtenerDestinoBus(@PathVariable Long busId) {
        try {
            Bus bus = busServicio.obtenerBusPorId(busId);
            if (bus == null) {
                return ResponseEntity.notFound().build();
            }

            // Obtener la siguiente parada de la ruta del bus
            Long rutaId = bus.getRuta().getId();
            List<Parada> paradasRuta = paradaServicio.obtenerParadasPorRutaOrdenadas(rutaId);

            // Obtener la última posición GPS del bus para determinar la parada más cercana
            GPSData ultimaPosicion = gpsDataServicio.obtenerUltimaUbicacionBus(busId);
            if (ultimaPosicion == null) {
                return ResponseEntity.notFound().build();
            }

            // Encontrar la siguiente parada basada en la posición actual
            Parada paradaDestino = busServicio.encontrarSiguienteParada(paradasRuta, ultimaPosicion.getLatitud(), ultimaPosicion.getLongitud()
            );

            if (paradaDestino != null) {
                return ResponseEntity.ok(paradaDestino);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
