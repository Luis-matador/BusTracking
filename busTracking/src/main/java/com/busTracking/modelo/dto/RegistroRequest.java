package com.busTracking.modelo.dto;

/**
 * Datos de alta de un usuario. {@code codigoAdmin} es opcional: si coincide con el código
 * configurado en el servidor, el usuario se crea como administrador.
 */
public record RegistroRequest(String nombre, String apellido, String email, String password, String codigoAdmin) {
}
