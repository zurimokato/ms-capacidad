package com.bootcamp.capacidad.domain.exception;

public class CapabilityAlreadyExistsException extends RuntimeException {

    public CapabilityAlreadyExistsException(String name) {
        super("La capacidad con nombre '" + name + "' ya existe");
    }
}
