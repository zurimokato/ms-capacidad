package com.bootcamp.capacidad.domain.spi;

import com.bootcamp.capacidad.domain.model.Capability;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICapabilityPersistencePort {

    Mono<Capability> save(Capability capability);

    Mono<Boolean> existsByName(String name);

    Flux<Capability> findAll(int page, int size, String sortBy, boolean ascending);

    Mono<Capability> findById(Long id);

    Mono<Void> saveCapabilityTechnologies(Long capabilityId, java.util.List<Long> technologyIds);
}
