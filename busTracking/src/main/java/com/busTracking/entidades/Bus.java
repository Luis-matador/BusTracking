package com.busTracking.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entidad que representa un autobús en el sistema.
 * Almacena información básica del vehículo y sus relaciones
 * con conductor, ruta y datos GPS.
 */
@Entity
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String marca;
    private String modelo;
    private Integer capacidad;
    private String matricula;

    /**
     * Relación uno a uno con el conductor asignado al autobús
     */
    @OneToOne(mappedBy = "busAsignado")
    @JsonIgnoreProperties("busAsignado")
    private Conductor conductor;

    /**
     * Relación con la ruta asignada al autobús
     */
    @ManyToOne
    @JsonIgnoreProperties("buses")
    private Ruta ruta;

    /**
     * Historial de datos GPS del autobús.
     * Se eliminan en cascada si se elimina el autobús.
     */
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true) // Relación con datos GPS
    private List<GPSData> datosGPS;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public List<GPSData> getDatosGPS() {
        return datosGPS;
    }

    public void setDatosGPS(List<GPSData> datosGPS) {
        this.datosGPS = datosGPS;
    }
}