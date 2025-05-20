package com.busTracking.modelo.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

/**
 * Entidad que representa una ruta de autobús en el sistema.
 * Contiene la información de la ruta y gestiona sus relaciones
 * con paradas y autobuses asignados.
 */
@Entity
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String info;

    /**
     * Lista de paradas que conforman la ruta.
     * Se eliminan en cascada si se elimina la ruta.
     */
    @OneToMany(mappedBy = "rutaAsociada", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("rutaAsociada")
    private List<Parada> paradas;

    /**
     * Lista de autobuses asignados a esta ruta.
     * Se eliminan en cascada si se elimina la ruta.
     */
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("ruta")
    private List<Bus> buses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Parada> getParadas() {
        return paradas;
    }

    public void setParadas(List<Parada> paradas) {
        this.paradas = paradas;
    }

    public List<Bus> getBuses() {
        return buses;
    }

    public void setBuses(List<Bus> buses) {
        this.buses = buses;
    }
}