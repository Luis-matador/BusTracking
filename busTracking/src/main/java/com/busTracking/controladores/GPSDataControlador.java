package com.busTracking.controladores;

import com.busTracking.entidades.GPSData;
import com.busTracking.servicios.GPSDataServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/gps")
public class GPSDataControlador {

    private final GPSDataServicio gpsDataServicio;


    @Autowired
    public GPSDataControlador(GPSDataServicio gpsDataServicio) {
        this.gpsDataServicio = gpsDataServicio;
    }


    @PostMapping
    public ResponseEntity<GPSData> registrarGPSData(@RequestBody GPSData gpsData) {
        if (gpsData.getTiempo() == null) {
            gpsData.setTiempo(LocalDateTime.now());
        }

        GPSData nuevoGPSData = gpsDataServicio.guardarGPSData(gpsData);
        return new ResponseEntity<>(nuevoGPSData, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<GPSData> obtenerGPSDataPorId(@PathVariable Long id) {
        GPSData gpsData = gpsDataServicio.obtenerGPSDataPorId(id);
        if (gpsData != null) {
            return ResponseEntity.ok(gpsData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping
    public ResponseEntity<List<GPSData>> obtenerTodosLosGPSData() {
        List<GPSData> gpsDataList = gpsDataServicio.obtenerTodosLosGPSData();
        return ResponseEntity.ok(gpsDataList);
    }


    @GetMapping("/ultimos")
    public ResponseEntity<List<GPSData>> obtenerUltimosGPSDataParaTodosBuses() {
        List<GPSData> ultimosGPSData = gpsDataServicio.obtenerUltimoGPSDataParaTodosLosBuses();
        return ResponseEntity.ok(ultimosGPSData);
    }


    @GetMapping("/bus/{busId}/ultimo")
    public ResponseEntity<GPSData> obtenerUltimoGPSDataPorBus(@PathVariable Long busId) {
        GPSData ultimoGPSData = gpsDataServicio.obtenerUltimoGPSDataPorBus(busId);
        if (ultimoGPSData != null) {
            return ResponseEntity.ok(ultimoGPSData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<GPSData>> obtenerGPSDataPorBusOrdenado(@PathVariable Long busId) {
        List<GPSData> gpsDataList = gpsDataServicio.obtenerGPSDataPorBusOrdenadoDesc(busId);
        return ResponseEntity.ok(gpsDataList);
    }


    @GetMapping("/bus/{busId}/rango")
    public ResponseEntity<List<GPSData>> obtenerGPSDataPorBusEnRangoDeTiempo(
            @PathVariable Long busId,
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        List<GPSData> gpsDataList = gpsDataServicio.obtenerGPSDataPorBusEnRangoDeTiempo(busId, inicio, fin);
        return ResponseEntity.ok(gpsDataList);
    }


    @PutMapping("/{id}")
    public ResponseEntity<GPSData> actualizarGPSData(@PathVariable Long id, @RequestBody GPSData gpsData) {
        GPSData gpsDataActualizado = gpsDataServicio.actualizarGPSData(id, gpsData);
        if (gpsDataActualizado != null) {
            return ResponseEntity.ok(gpsDataActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGPSData(@PathVariable Long id) {
        GPSData gpsData = gpsDataServicio.obtenerGPSDataPorId(id);
        if (gpsData == null) {
            return ResponseEntity.notFound().build();
        }
        gpsDataServicio.eliminarGPSData(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/batch")
    public ResponseEntity<List<GPSData>> registrarGPSDataEnLote(@RequestBody List<GPSData> gpsDataList) {

        for (GPSData gpsData : gpsDataList) {
            if (gpsData.getTiempo() == null) {
                gpsData.setTiempo(LocalDateTime.now());
            }
        }

        List<GPSData> savedList = gpsDataList.stream().map(gpsDataServicio::guardarGPSData).toList();

        return new ResponseEntity<>(savedList, HttpStatus.CREATED);
    }
}