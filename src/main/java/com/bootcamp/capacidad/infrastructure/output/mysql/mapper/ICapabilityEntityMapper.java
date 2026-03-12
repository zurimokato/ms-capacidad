package com.bootcamp.capacidad.infrastructure.output.mysql.mapper;

import com.bootcamp.capacidad.domain.model.Capability;
import com.bootcamp.capacidad.infrastructure.output.mysql.entity.CapabilityEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ICapabilityEntityMapper {

    @Mapping(target = "technologies", ignore = true)
    Capability toDomain(CapabilityEntity entity);

    CapabilityEntity toEntity(Capability capability);
}
