package com.busTracking.dto;

import com.busTracking.entidades.GPSData;
import java.time.LocalDateTime;

public class GPSDataDTO {
    private Long id;
    private Double latitud;
    private Double longitud;
    private Double velocidad;
    private Double direccion;
    private LocalDateTime tiempo;
    private Long busId;
    private String busMatricula;
    private String rutaNombre;  // Solo incluimos el nombre de la ruta, no toda la entidad

    // Constructor vacío necesario para la serialización
    public GPSDataDTO() {
    }

    // Constructor que convierte desde la entidad
    public GPSDataDTO(GPSData gpsData) {
        this.id = gpsData.getId();
        this.latitud = gpsData.getLatitud();
        this.longitud = gpsData.getLongitud();
        this.velocidad = gpsData.getVelocidad();
        this.direccion = gpsData.getDireccion();
        this.tiempo = gpsData.getTiempo();

        if (gpsData.getBus() != null) {
            this.busId = gpsData.getBus().getId();
            this.busMatricula = gpsData.getBus().getMatricula();

            // Solo incluimos información básica de la ruta si existe
            if (gpsData.getBus().getRuta() != null) {
                this.rutaNombre = gpsData.getBus().getRuta().getNombre();
            }
        }
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(Double velocidad) {
        this.velocidad = velocidad;
    }

    public Double getDireccion() {
        return direccion;
    }

    public void setDireccion(Double direccion) {
        this.direccion = direccion;
    }

    public LocalDateTime getTiempo() {
        return tiempo;
    }

    public void setTiempo(LocalDateTime tiempo) {
        this.tiempo = tiempo;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public String getBusMatricula() {
        return busMatricula;
    }

    public void setBusMatricula(String busMatricula) {
        this.busMatricula = busMatricula;
    }

    public String getRutaNombre() {
        return rutaNombre;
    }

    public void setRutaNombre(String rutaNombre) {
        this.rutaNombre = rutaNombre;
    }
}