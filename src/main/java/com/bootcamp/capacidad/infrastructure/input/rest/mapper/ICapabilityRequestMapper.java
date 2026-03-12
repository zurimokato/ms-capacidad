package com.bootcamp.capacidad.infrastructure.input.rest.mapper;

import com.bootcamp.capacidad.domain.model.Capability;
import com.bootcamp.capacidad.domain.model.Technology;
import com.bootcamp.capacidad.infrastructure.input.rest.dto.CapabilityRequest;
import com.bootcamp.capacidad.infrastructure.input.rest.dto.CapabilityResponse;
import com.bootcamp.capacidad.infrastructure.input.rest.dto.TechnologySimpleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ICapabilityRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "technologies", source = "technologyIds", qualifiedByName = "idsToTechnologies")
    Capability toDomain(CapabilityRequest request);

    @Mapping(target = "technologies", source = "technologies", qualifiedByName = "techToSimple")
    CapabilityResponse toResponse(Capability capability);

    @Named("idsToTechnologies")
    default List<Technology> idsToTechnologies(List<Long> ids) {
        if (ids == null) return List.of();
        return ids.stream()
                .map(id -> new Technology(id, null))
                .collect(Collectors.toList());
    }

    @Named("techToSimple")
    default List<TechnologySimpleResponse> techToSimple(List<Technology> technologies) {
        if (technologies == null) return List.of();
        return technologies.stream()
                .map(t -> new TechnologySimpleResponse(t.getId(), t.getName()))
                .collect(Collectors.toList());
    }
}
