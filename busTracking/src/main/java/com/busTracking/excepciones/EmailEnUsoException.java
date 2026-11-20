package com.busTracking.excepciones;

public class EmailEnUsoException extends RuntimeException {
    public EmailEnUsoException(String email) {
        super("El email " + email + " ya está en uso");
    }
}
