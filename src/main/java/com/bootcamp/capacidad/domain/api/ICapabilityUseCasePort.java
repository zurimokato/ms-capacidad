package com.bootcamp.capacidad.domain.api;

import com.bootcamp.capacidad.domain.model.Capability;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICapabilityUseCasePort {

    Mono<Capability> save(Capability capability);

    Flux<Capability> findAll(int page, int size, String sortBy, boolean ascending);
}
