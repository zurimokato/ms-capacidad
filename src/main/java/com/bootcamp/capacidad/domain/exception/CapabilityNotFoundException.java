package com.bootcamp.capacidad.domain.exception;

public class CapabilityNotFoundException extends RuntimeException {

    public CapabilityNotFoundException(Long id) {
        super("No se encontró la capacidad con id: " + id);
    }
}
