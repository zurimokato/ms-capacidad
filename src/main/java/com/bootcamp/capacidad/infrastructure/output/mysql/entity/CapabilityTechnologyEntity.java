package com.bootcamp.capacidad.infrastructure.output.mysql.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("capacidad_tecnologias")
public class CapabilityTechnologyEntity {

    @Id
    private Long id;

    @Column("id_capacidad")
    private Long capabilityId;

    @Column("id_tecnologia")
    private Long technologyId;

    public CapabilityTechnologyEntity() {
    }

    public CapabilityTechnologyEntity(Long capabilityId, Long technologyId) {
        this.capabilityId = capabilityId;
        this.technologyId = technologyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(Long capabilityId) {
        this.capabilityId = capabilityId;
    }

    public Long getTechnologyId() {
        return technologyId;
    }

    public void setTechnologyId(Long technologyId) {
        this.technologyId = technologyId;
    }
}
