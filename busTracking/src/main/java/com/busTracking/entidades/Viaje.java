package com.busTracking.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime inicio;  // Hora de inicio del viaje
    private LocalDateTime fin;     // Hora de finalización (opcional)

    @ManyToOne
    private Pasajero pasajero;     // Pasajero que realiza el viaje

    @ManyToOne
    private Bus bus;               // Autobús utilizado en el viaje

    @ManyToOne
    private Ruta ruta;             // Ruta que se tomó en el viaje

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public void setPasajero(Pasajero pasajero) {
        this.pasajero = pasajero;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }
}