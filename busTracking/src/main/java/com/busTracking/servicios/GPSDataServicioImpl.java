package com.busTracking.servicios;

import com.busTracking.dto.GPSDataDTO;
import com.busTracking.entidades.GPSData;
import com.busTracking.repositorios.BusRepositorio;
import com.busTracking.repositorios.GPSDataRepositorio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GPSDataServicioImpl implements GPSDataServicio {

    private final GPSDataRepositorio gpsDataRepositorio;
    private final BusRepositorio busRepositorio;
    private final SimpMessagingTemplate messagingTemplate;

    public GPSDataServicioImpl(GPSDataRepositorio gpsDataRepositorio, BusRepositorio busRepositorio, SimpMessagingTemplate messagingTemplate) {
        this.gpsDataRepositorio = gpsDataRepositorio;
        this.busRepositorio = busRepositorio;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    @Transactional
    public GPSData guardarGPSData(GPSData gpsData) {
        if (gpsData.getBus() != null) {
            Long busId = gpsData.getBus().getId();
            if (!busRepositorio.existsById(busId)) {
                throw new EntityNotFoundException("No se encontró bus con ID: " + busId);
            }
        }
        if (gpsData.getTiempo() == null) {
            gpsData.setTiempo(LocalDateTime.now());
        }
        validarDatosGPS(gpsData);

        GPSData nuevoGPSData = gpsDataRepositorio.save(gpsData);

        if (nuevoGPSData.getBus() != null) {
            GPSDataDTO dto = new GPSDataDTO(nuevoGPSData);
            messagingTemplate.convertAndSend("/topic/posicion-bus/" + nuevoGPSData.getBus().getId(), dto);
        }

        return nuevoGPSData;
    }



    @Scheduled(fixedRate = 5000)
    public void enviarActualizacionesPosicion() {
        try {
            List<GPSData> posicionesBuses = obtenerUltimoGPSDataParaTodosLosBuses();

            List<GPSDataDTO> dtos = new ArrayList<>();
            for (GPSData gpsData : posicionesBuses) {
                dtos.add(new GPSDataDTO(gpsData));
            }

            messagingTemplate.convertAndSend("/topic/posiciones-buses", dtos);
        } catch (Exception e) {
            System.err.println("Error al enviar actualizaciones periódicas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 30000) // Cada 30 segundos
    @Transactional
    public void limpiarDatosGPSPeriodicamente() {
        try {
            gpsDataRepositorio.deleteAll();
            gpsDataRepositorio.resetAutoIncrement();
            System.out.println("Todos los datos de la tabla GPSData han sido eliminados.");
        } catch (Exception e) {
            System.err.println("Error al limpiar los datos de GPSData: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public GPSData obtenerGPSDataPorId(Long id) {
        return gpsDataRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("GPSData con id " + id + " no encontrada"));
    }

    @Override
    public List<GPSData> obtenerTodosLosGPSData() {
        return gpsDataRepositorio.findAll();
    }

    @Override
    public GPSData obtenerUltimoGPSDataPorBus(Long busId) {
        if (!busRepositorio.existsById(busId)) {
            throw new EntityNotFoundException("No se encontró bus con ID: " + busId);
        }

        GPSData ultimoGPSData = gpsDataRepositorio.findLastGPSDataForBus(busId);
        if (ultimoGPSData == null) {
            throw new EntityNotFoundException("No hay datos GPS registrados para el bus con ID: " + busId);
        }

        return ultimoGPSData;
    }

    @Override
    public GPSData obtenerUltimaPosicionPorBusId(Long busId) {
        try {
            if (!busRepositorio.existsById(busId)) {
                System.err.println("No se encontró bus con ID: " + busId);
                return null;
            }
            try {
                return gpsDataRepositorio.findLastGPSDataForBus(busId);
            } catch (Exception e) {

                return gpsDataRepositorio.findTopByBusIdOrderByTiempoDesc(busId);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener última posición para bus " + busId + ": " + e.getMessage());
            return null;
        }
    }

    public GPSData obtenerUltimaUbicacionBus(Long busId) {
        return gpsDataRepositorio.findFirstByBusIdOrderByTiempoDesc(busId);
    }

    @Override
    public List<GPSData> obtenerGPSDataPorBusOrdenadoDesc(Long busId) {
        if (!busRepositorio.existsById(busId)) {
            throw new EntityNotFoundException("No se encontró bus con ID: " + busId);
        }

        return gpsDataRepositorio.findByBusIdOrderByTiempoDesc(busId);
    }

    @Override
    public List<GPSData> obtenerGPSDataPorBusEnRangoDeTiempo(Long busId, LocalDateTime inicio, LocalDateTime fin) {
        if (!busRepositorio.existsById(busId)) {
            throw new EntityNotFoundException("No se encontró bus con ID: " + busId);
        }

        if (inicio != null && fin != null && inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        return gpsDataRepositorio.findGPSDataByBusInTimeRange(busId, inicio, fin);
    }

    @Override
    public List<GPSData> obtenerUltimoGPSDataParaTodosLosBuses() {
        return gpsDataRepositorio.findLastGPSDataForAllBuses();
    }

    @Transactional
    @Override
    public GPSData actualizarGPSData(Long id, GPSData gpsData) {
        GPSData gpsDataExistente = gpsDataRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("GPSData con id " + id + " no encontrada"));

        if (gpsData.getLatitud() != null) {
            gpsDataExistente.setLatitud(gpsData.getLatitud());
        }

        if (gpsData.getLongitud() != null) {
            gpsDataExistente.setLongitud(gpsData.getLongitud());
        }

        if (gpsData.getVelocidad() != null) {
            gpsDataExistente.setVelocidad(gpsData.getVelocidad());
        }

        if (gpsData.getDireccion() != null) {
            gpsDataExistente.setDireccion(gpsData.getDireccion());
        }

        if (gpsData.getTiempo() != null) {
            gpsDataExistente.setTiempo(gpsData.getTiempo());
        }

        if (gpsData.getBus() != null) {
            Long busId = gpsData.getBus().getId();
            if (!busRepositorio.existsById(busId)) {
                throw new EntityNotFoundException("No se encontró bus con ID: " + busId);
            }
            gpsDataExistente.setBus(gpsData.getBus());
        }

        validarDatosGPS(gpsDataExistente);

        GPSData gpsActualizado = gpsDataRepositorio.save(gpsDataExistente);

        // Enviamos notificación de posición GPS actualizada como DTO
        if (gpsActualizado.getBus() != null) {
            // Creamos un DTO para evitar problemas de LazyInitializationException
            GPSDataDTO dto = new GPSDataDTO(gpsActualizado);
            messagingTemplate.convertAndSend("/topic/posicion-bus/" + gpsActualizado.getBus().getId(), dto);
        }

        return gpsActualizado;
    }

    @Transactional
    @Override
    public void eliminarGPSData(Long id) {
        if (!gpsDataRepositorio.existsById(id)) {
            throw new EntityNotFoundException("GPSData con id " + id + " no encontrada");
        }
        gpsDataRepositorio.deleteById(id);
    }

    private void validarDatosGPS(GPSData gpsData) {
        if (gpsData.getLatitud() != null && (gpsData.getLatitud() < -90 || gpsData.getLatitud() > 90)) {
            throw new IllegalArgumentException("La latitud debe estar entre -90 y 90 grados");
        }

        if (gpsData.getLongitud() != null && (gpsData.getLongitud() < -180 || gpsData.getLongitud() > 180)) {
            throw new IllegalArgumentException("La longitud debe estar entre -180 y 180 grados");
        }

        if (gpsData.getVelocidad() != null && gpsData.getVelocidad() < 0) {
            throw new IllegalArgumentException("La velocidad no puede ser negativa");
        }

        if (gpsData.getDireccion() != null && (gpsData.getDireccion() < 0 || gpsData.getDireccion() > 360)) {
            throw new IllegalArgumentException("La dirección debe estar entre 0 y 360 grados");
        }
    }
}