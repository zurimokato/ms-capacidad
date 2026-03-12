package com.bootcamp.capacidad.infrastructure.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CapabilityRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotEmpty(message = "Las tecnologías son obligatorias")
    @Size(min = 3, max = 20, message = "Debe tener entre 3 y 20 tecnologías")
    private List<Long> technologyIds;

    public CapabilityRequest() {
    }

    public CapabilityRequest(String name, String description, List<Long> technologyIds) {
        this.name = name;
        this.description = description;
        this.technologyIds = technologyIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getTechnologyIds() {
        return technologyIds;
    }

    public void setTechnologyIds(List<Long> technologyIds) {
        this.technologyIds = technologyIds;
    }
}
