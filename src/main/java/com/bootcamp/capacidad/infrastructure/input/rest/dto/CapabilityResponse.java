package com.bootcamp.capacidad.infrastructure.input.rest.dto;

import java.util.List;

public class CapabilityResponse {

    private Long id;
    private String name;
    private String description;
    private List<TechnologySimpleResponse> technologies;

    public CapabilityResponse() {
    }

    public CapabilityResponse(Long id, String name, String description, List<TechnologySimpleResponse> technologies) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.technologies = technologies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<TechnologySimpleResponse> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<TechnologySimpleResponse> technologies) {
        this.technologies = technologies;
    }
}
