package com.bootcamp.capacidad.domain.exception;

public final class DomainConstants {

    private DomainConstants() {
    }

    public static final int MIN_TECHNOLOGIES = 3;
    public static final int MAX_TECHNOLOGIES = 20;
    public static final String NAME_REQUIRED = "El nombre es obligatorio";
    public static final String DESCRIPTION_REQUIRED = "La descripción es obligatoria";
    public static final String TECHNOLOGIES_REQUIRED = "Las tecnologías son obligatorias";
    public static final String MIN_TECHNOLOGIES_MSG = "Una capacidad debe tener mínimo 3 tecnologías asociadas";
    public static final String MAX_TECHNOLOGIES_MSG = "Una capacidad tiene máximo 20 tecnologías";
    public static final String DUPLICATE_TECHNOLOGIES_MSG = "Las capacidades no pueden tener tecnologías repetidas";
}
