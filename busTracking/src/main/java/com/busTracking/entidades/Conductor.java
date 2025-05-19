package com.busTracking.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

/**
 * Entidad que representa a un conductor en el sistema.
 * Almacena información personal del conductor y su relación
 * con el autobús que tiene asignado.
 */
@Entity
public class Conductor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String dni;
    private String tel;

    /**
     * Relación uno a uno con el autobús asignado al conductor.
     * Se evita la serialización circular con JsonIgnoreProperties.
     */
    @OneToOne
    @JsonIgnoreProperties("conductor")
    private Bus busAsignado;

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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Bus getBusAsignado() {
        return busAsignado;
    }

    public void setBusAsignado(Bus busAsignado) {
        this.busAsignado = busAsignado;
    }
}