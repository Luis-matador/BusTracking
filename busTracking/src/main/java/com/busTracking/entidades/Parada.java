package com.busTracking.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * Entidad que representa una parada de autobús en el sistema.
 * Almacena la ubicación y orden de las paradas dentro de una ruta.
 */
@Entity
public class Parada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Double latitud;
    private Double longitud;
    private Integer orden;

    /**
     * Relación con la ruta a la que pertenece esta parada.
     * Se evita la serialización circular con JsonIgnoreProperties.
     */
    @ManyToOne
    @JsonIgnoreProperties("paradas")
    private Ruta rutaAsociada;

    // Getters y Setters
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

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Ruta getRutaAsociada() {
        return rutaAsociada;
    }

    public void setRutaAsociada(Ruta rutaAsociada) {
        this.rutaAsociada = rutaAsociada;
    }
}