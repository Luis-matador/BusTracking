package com.busTracking.modelo.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

/**
 * Entidad que almacena los datos de ubicación GPS de los autobuses.
 * Registra la posición, velocidad, dirección y tiempo de cada lectura GPS.
 */
@Entity
public class GPSData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitud;         // Latitud de la ubicación
    private Double longitud;        // Longitud de la ubicación
    private LocalDateTime tiempo;   // Fecha y hora del registro
    private Double velocidad;       // Velocidad del autobús (km/h)
    private Double direccion;       // Dirección en grados (0-360)

    /**
     * Referencia al autobús al que pertenecen estos datos GPS.
     * Se excluye de la serialización JSON para evitar ciclos.
     */
    @ManyToOne // Relación con el autobús
    @JsonIgnore
    private Bus bus;

    // Getters y Setters
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

    public LocalDateTime getTiempo() {
        return tiempo;
    }

    public void setTiempo(LocalDateTime tiempo) {this.tiempo = tiempo;}

    public Double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(Double velocidad) {this.velocidad = velocidad;}

    public Double getDireccion() {
        return direccion;
    }

    public void setDireccion(Double direccion) {
        this.direccion = direccion;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }
}