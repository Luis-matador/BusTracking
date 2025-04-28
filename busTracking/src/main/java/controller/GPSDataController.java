package controller;

import model.GPSData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.GPSDataService;
import config.WebSocketHandler;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gpsData")
public class GPSDataController {

    private final GPSDataService gpsDataService;

    private final WebSocketHandler webSocketHandler; // Handler para manejar WebSockets

    @Autowired
    public GPSDataController(GPSDataService gpsDataService, WebSocketHandler webSocketHandler) {
        this.gpsDataService = gpsDataService;
        this.webSocketHandler = webSocketHandler; // Inyección del WebSocketHandler
    }

    // Obtener todos los registros de GPSData
    @GetMapping
    public List<GPSData> getAllGPSData() {
        return gpsDataService.getAllGPSData();
    }

    // Obtener un registro GPSData por su ID
    @GetMapping("/{id}")
    public GPSData getGPSDataById(@PathVariable Long id) {
        return gpsDataService.getGPSDataById(id).orElse(null);
    }

    // Crear un nuevo registro GPSData
    @PostMapping
    public ResponseEntity<GPSData> createGPSData(@RequestBody GPSData gpsData) {
        // Guardar el registro en la base de datos
        GPSData savedData = gpsDataService.saveGPSData(gpsData);

        // Enviar una actualización a los clientes conectados usando WebSocket
        try {
            String message = "Nuevo GPSData: " + gpsData.getBusId() +
                    " en (" + gpsData.getLatitude() + ", " + gpsData.getLongitude() + ")";
            webSocketHandler.sendUpdates(message); // Notificamos por el WebSocket
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Responder con el registro creado y un código HTTP 201 (CREATED)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedData);
    }

    // Eliminar un registro GPSData por su ID
    @DeleteMapping("/{id}")
    public void deleteGPSData(@PathVariable Long id) {
        gpsDataService.deleteGPSData(id);
    }
}