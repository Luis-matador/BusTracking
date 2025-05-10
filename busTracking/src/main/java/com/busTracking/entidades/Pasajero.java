package com.busTracking.entidades;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Pasajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;             // Nombre del pasajero
    private String apellido;           // Apellido del pasajero
    private String telefono;           // Teléfono del pasajero
    private Integer numViajes = 0;     // Número de viajes realizados por el pasajero

    @OneToMany(mappedBy = "pasajero", cascade = CascadeType.ALL, orphanRemoval = true) // Relación con viajes
    private List<Viaje> viajes;

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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getNumViajes() {
        return numViajes;
    }

    public void setNumViajes(Integer numViajes) {
        this.numViajes = numViajes;
    }

    public List<Viaje> getViajes() {
        return viajes;
    }

    public void setViajes(List<Viaje> viajes) {
        this.viajes = viajes;
    }
}