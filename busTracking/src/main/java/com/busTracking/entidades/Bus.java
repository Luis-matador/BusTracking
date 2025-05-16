package com.busTracking.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String marca;
    private String modelo;
    private Integer capacidad;
    private String matricula;

    @OneToOne(mappedBy = "busAsignado") // Relación con el conductor
    @JsonIgnoreProperties("busAsignado")
    private Conductor conductor;

    @ManyToOne // Relación con la ruta
    @JsonIgnoreProperties("buses")
    private Ruta ruta;

    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL, orphanRemoval = true) // Relación con datos GPS
    private List<GPSData> datosGPS;

    @OneToMany // Relación con pasajeros (opcional)
    private List<Pasajero> pasajeros;

    // Getters y Setters
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

    public List<Pasajero> getPasajeros() {
        return pasajeros;
    }

    public void setPasajeros(List<Pasajero> pasajeros) {
        this.pasajeros = pasajeros;
    }
}